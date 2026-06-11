package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface WordDao {
    @Query("SELECT * FROM dictionary_words WHERE language = :language AND isApproved = 1 ORDER BY word ASC")
    fun getAllApprovedWords(language: String): Flow<List<DictionaryWord>>

    @Query("SELECT * FROM dictionary_words WHERE isApproved = 0 ORDER BY id DESC")
    fun getPendingWordsFlow(): Flow<List<DictionaryWord>>

    @Query("SELECT * FROM dictionary_words WHERE isApproved = 0 ORDER BY id DESC")
    suspend fun getPendingWords(): List<DictionaryWord>

    @Query("SELECT * FROM dictionary_words WHERE isFavorite = 1 ORDER BY word ASC")
    fun getBookmarksFlow(): Flow<List<DictionaryWord>>

    @Query("""
        SELECT * FROM dictionary_words 
        WHERE language = :language AND isApproved = 1 AND word LIKE :query || '%'
        UNION
        SELECT * FROM dictionary_words 
        WHERE language = :language AND isApproved = 1 AND word LIKE '%' || :query || '%'
        LIMIT 50
    """)
    fun searchWords(query: String, language: String): Flow<List<DictionaryWord>>

    @Query("SELECT word FROM dictionary_words WHERE language = :language AND isApproved = 1 AND word LIKE :query || '%' LIMIT 10")
    fun getSearchSuggestions(query: String, language: String): Flow<List<String>>

    @Query("SELECT * FROM dictionary_words WHERE id = :id")
    suspend fun getWordById(id: Int): DictionaryWord?

    @Query("SELECT * FROM dictionary_words WHERE word = :word AND language = :language LIMIT 1")
    suspend fun getWordByValue(word: String, language: String): DictionaryWord?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWord(word: DictionaryWord): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWords(words: List<DictionaryWord>)

    @Update
    suspend fun updateWord(word: DictionaryWord)

    @Delete
    suspend fun deleteWord(word: DictionaryWord)

    @Query("UPDATE dictionary_words SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateBookmarkState(id: Int, isFavorite: Boolean)

    @Query("UPDATE dictionary_words SET isApproved = 1 WHERE id = :id")
    suspend fun approveWord(id: Int)

    @Query("UPDATE dictionary_words SET searchCount = searchCount + 1 WHERE id = :id")
    suspend fun incrementSearchCount(id: Int)

    @Query("SELECT * FROM dictionary_words WHERE searchCount > 0 ORDER BY searchCount DESC LIMIT 30")
    fun getMostPopularWords(): Flow<List<DictionaryWord>>
}
