package com.example.ui

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ads.AdManager
import com.example.data.*
import com.example.util.TtsHelper
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DictionaryViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application, viewModelScope)
    private val repository = DictionaryRepository(db.wordDao(), db.historyDao())

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _language = MutableStateFlow("en_to_bn") // "en_to_bn" or "bn_to_en"
    val language: StateFlow<String> = _language.asStateFlow()

    // Active Results Flow
    val searchResults: StateFlow<List<DictionaryWord>> = combine(_searchQuery, _language) { query, lang ->
        Pair(query, lang)
    }.flatMapLatest { (query, lang) ->
        if (query.isBlank()) {
            // When query is empty, return empty list or we can show suggestions below
            flowOf(emptyList())
        } else {
            repository.searchWords(query, lang)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Search suggestions
    val suggestions: StateFlow<List<String>> = combine(_searchQuery, _language) { query, lang ->
        Pair(query, lang)
    }.flatMapLatest { (query, lang) ->
        if (query.isBlank() || query.length < 2) {
            flowOf(emptyList())
        } else {
            repository.getSearchSuggestions(query, lang)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Bookmarks Flow
    val bookmarks: StateFlow<List<DictionaryWord>> = repository.getBookmarksFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Search History Flow
    val searchHistory: StateFlow<List<SearchHistory>> = repository.searchHistory
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Popular Words Flow
    val popularWords: StateFlow<List<DictionaryWord>> = repository.getPopularWords()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Pending words list for approvals
    val pendingWords: StateFlow<List<DictionaryWord>> = repository.getPendingWordsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Selected Word details
    private val _selectedWord = MutableStateFlow<DictionaryWord?>(null)
    val selectedWord: StateFlow<DictionaryWord?> = _selectedWord.asStateFlow()

    // Analytics Dashboard Stats
    private val _totalWordsCount = MutableStateFlow(0)
    val totalWordsCount: StateFlow<Int> = _totalWordsCount.asStateFlow()

    private val _enToBnCount = MutableStateFlow(0)
    val enToBnCount: StateFlow<Int> = _enToBnCount.asStateFlow()

    private val _bnToEnCount = MutableStateFlow(0)
    val bnToEnCount: StateFlow<Int> = _bnToEnCount.asStateFlow()

    // Admin login code setup (default: "admin123")
    private val _isAdminLoggedIn = MutableStateFlow(false)
    val isAdminLoggedIn: StateFlow<Boolean> = _isAdminLoggedIn.asStateFlow()

    // TTS voice service
    private var ttsHelper: TtsHelper? = null

    init {
        // Initialize TTS
        ttsHelper = TtsHelper(application)
        updateAnalytics()
    }

    fun setLanguage(lang: String) {
        _language.value = lang
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectWord(word: DictionaryWord) {
        _selectedWord.value = word
        // Record details
        viewModelScope.launch {
            repository.incrementSearchCount(word.id)
            repository.addToHistory(word.word)
            updateAnalytics()
        }
    }

    fun clearSelectedWord() {
        _selectedWord.value = null
    }

    fun toggleBookmark(word: DictionaryWord) {
        viewModelScope.launch {
            repository.updateBookmark(word.id, !word.isFavorite)
            // also update current selectedWord if it matches to reflect immediately
            if (_selectedWord.value?.id == word.id) {
                _selectedWord.value = _selectedWord.value?.copy(isFavorite = !word.isFavorite)
            }
        }
    }

    fun speakWord(text: String, isBangla: Boolean) {
        ttsHelper?.speak(text, isBangla)
    }

    fun clearSearchHistory() {
        viewModelScope.launch {
            repository.clearAllHistory()
        }
    }

    fun deleteHistoryItem(word: String) {
        viewModelScope.launch {
            repository.deleteHistoryItem(word)
        }
    }

    suspend fun getWordByValue(word: String, language: String): DictionaryWord? {
        return repository.getWordByValue(word, language)
    }

    // Admin / Core DB Functions
    fun loginAdmin(code: String): Boolean {
        return if (code == "admin123") {
            _isAdminLoggedIn.value = true
            true
        } else {
            false
        }
    }

    fun logoutAdmin() {
        _isAdminLoggedIn.value = false
    }

    fun addNewWord(
        word: String,
        translation: String,
        pronunciation: String,
        partOfSpeech: String,
        definition: String,
        example: String,
        exampleTrans: String,
        synonymsList: String,
        antonymsList: String,
        langDirection: String,
        autoApprove: Boolean = false
    ) {
        viewModelScope.launch {
            val newWordEntity = DictionaryWord(
                word = word.trim(),
                translation = translation.trim(),
                pronunciation = pronunciation.trim(),
                partOfSpeech = partOfSpeech.trim(),
                definition = definition.trim(),
                exampleSentence = example.trim(),
                exampleTranslation = exampleTrans.trim(),
                synonyms = synonymsList.trim(),
                antonyms = antonymsList.trim(),
                language = langDirection,
                isApproved = autoApprove || _isAdminLoggedIn.value
            )
            repository.insertWord(newWordEntity)
            updateAnalytics()
        }
    }

    fun updateWord(updatedWord: DictionaryWord) {
        viewModelScope.launch {
            repository.updateWord(updatedWord)
            if (_selectedWord.value?.id == updatedWord.id) {
                _selectedWord.value = updatedWord
            }
            updateAnalytics()
        }
    }

    fun deleteWord(word: DictionaryWord) {
        viewModelScope.launch {
            repository.deleteWord(word)
            if (_selectedWord.value?.id == word.id) {
                _selectedWord.value = null
            }
            updateAnalytics()
        }
    }

    fun approvePendingWord(wordId: Int) {
        viewModelScope.launch {
            repository.approveWord(wordId)
            updateAnalytics()
        }
    }

    // Refresh dashboard stats directly from the SQLite database
    fun updateAnalytics() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                // Fetch direct counts using Raw queries or standard repository reads
                // To keep database file light, read counts from DAO/SQLite
                val cursor = db.openHelper.readableDatabase.query("SELECT COUNT(*) FROM dictionary_words")
                var total = 0
                if (cursor.moveToFirst()) {
                    total = cursor.getInt(0)
                }
                cursor.close()

                val cursorEn = db.openHelper.readableDatabase.query("SELECT COUNT(*) FROM dictionary_words WHERE language = 'en_to_bn'")
                var enCount = 0
                if (cursorEn.moveToFirst()) {
                    enCount = cursorEn.getInt(0)
                }
                cursorEn.close()

                val cursorBn = db.openHelper.readableDatabase.query("SELECT COUNT(*) FROM dictionary_words WHERE language = 'bn_to_en'")
                var bnCount = 0
                if (cursorBn.moveToFirst()) {
                    bnCount = cursorBn.getInt(0)
                }
                cursorBn.close()

                _totalWordsCount.value = total
                _enToBnCount.value = enCount
                _bnToEnCount.value = bnCount
            }
        }
    }

    // Bulk Import using JSON string
    fun importFromJson(jsonString: String): Boolean {
        return try {
            val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
            val listType = Types.newParameterizedType(List::class.java, Map::class.java)
            val adapter = moshi.adapter<List<Map<String, Any>>>(listType)
            val rawList = adapter.fromJson(jsonString) ?: return false

            viewModelScope.launch {
                val wordsList = rawList.map { item ->
                    DictionaryWord(
                        word = (item["word"] as? String) ?: "",
                        translation = (item["translation"] as? String) ?: "",
                        pronunciation = (item["pronunciation"] as? String) ?: "",
                        partOfSpeech = (item["partOfSpeech"] as? String) ?: "Noun",
                        definition = (item["definition"] as? String) ?: "",
                        exampleSentence = (item["exampleSentence"] as? String) ?: "",
                        exampleTranslation = (item["exampleTranslation"] as? String) ?: "",
                        synonyms = (item["synonyms"] as? String) ?: "",
                        antonyms = (item["antonyms"] as? String) ?: "",
                        language = (item["language"] as? String) ?: "en_to_bn",
                        isApproved = true // Admin-imported are pre-approved
                    )
                }.filter { it.word.isNotEmpty() && it.translation.isNotEmpty() }

                repository.insertWords(wordsList)
                updateAnalytics()
            }
            true
        } catch (e: Exception) {
            Log.e("DictionaryViewModel", "JSON import error", e)
            false
        }
    }

    // CSV Bulk Import Function
    fun importFromCsv(csvString: String): Boolean {
        return try {
            val lines = csvString.split("\n")
            val wordsList = mutableListOf<DictionaryWord>()
            for (line in lines) {
                if (line.trim().isEmpty()) continue
                val parts = line.split(",")
                if (parts.size >= 3) {
                    val wordVal = parts[0].trim()
                    val transVal = parts[1].trim()
                    val langVal = parts[2].trim() // "en_to_bn" or "bn_to_en"
                    val posVal = if (parts.size >= 4) parts[3].trim() else "Noun"
                    val defVal = if (parts.size >= 5) parts[4].trim() else ""

                    if (wordVal.isNotEmpty() && transVal.isNotEmpty()) {
                        wordsList.add(
                            DictionaryWord(
                                word = wordVal,
                                translation = transVal,
                                language = langVal,
                                partOfSpeech = posVal,
                                definition = defVal,
                                isApproved = true
                            )
                        )
                    }
                }
            }
            if (wordsList.isNotEmpty()) {
                viewModelScope.launch {
                    repository.insertWords(wordsList)
                    updateAnalytics()
                }
                true
            } else {
                false
            }
        } catch (e: Exception) {
            Log.e("DictionaryViewModel", "CSV import error", e)
            false
        }
    }

    override fun onCleared() {
        super.onCleared()
        ttsHelper?.shutdown()
    }
}
