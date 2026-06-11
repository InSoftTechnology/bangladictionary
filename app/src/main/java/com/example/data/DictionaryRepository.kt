package com.example.data

import kotlinx.coroutines.flow.Flow

class DictionaryRepository(
    private val wordDao: WordDao,
    private val historyDao: HistoryDao
) {
    // Flows
    fun getApprovedWords(language: String): Flow<List<DictionaryWord>> = wordDao.getAllApprovedWords(language)
    fun getPendingWordsFlow(): Flow<List<DictionaryWord>> = wordDao.getPendingWordsFlow()
    fun getBookmarksFlow(): Flow<List<DictionaryWord>> = wordDao.getBookmarksFlow()
    fun getPopularWords(): Flow<List<DictionaryWord>> = wordDao.getMostPopularWords()
    val searchHistory: Flow<List<SearchHistory>> = historyDao.getRecentSearchHistory()

    // Words DB operations
    fun searchWords(query: String, language: String): Flow<List<DictionaryWord>> = wordDao.searchWords(query, language)
    fun getSearchSuggestions(query: String, language: String): Flow<List<String>> = wordDao.getSearchSuggestions(query, language)

    suspend fun getWordById(id: Int): DictionaryWord? = wordDao.getWordById(id)
    suspend fun getWordByValue(word: String, language: String): DictionaryWord? = wordDao.getWordByValue(word, language)

    suspend fun insertWord(word: DictionaryWord): Long = wordDao.insertWord(word)
    suspend fun insertWords(words: List<DictionaryWord>) = wordDao.insertWords(words)
    suspend fun updateWord(word: DictionaryWord) = wordDao.updateWord(word)
    suspend fun deleteWord(word: DictionaryWord) = wordDao.deleteWord(word)
    
    suspend fun updateBookmark(id: Int, isFavorite: Boolean) = wordDao.updateBookmarkState(id, isFavorite)
    suspend fun approveWord(id: Int) = wordDao.approveWord(id)
    suspend fun incrementSearchCount(id: Int) = wordDao.incrementSearchCount(id)

    // History operations
    suspend fun addToHistory(word: String) {
        historyDao.insertSearch(SearchHistory(word = word))
    }
    suspend fun deleteHistoryItem(word: String) {
        historyDao.deleteSearch(word)
    }
    suspend fun clearAllHistory() {
        historyDao.clearHistory()
    }

    // Pending words list for approvals
    suspend fun getPendingWordsList(): List<DictionaryWord> = wordDao.getPendingWords()
}
