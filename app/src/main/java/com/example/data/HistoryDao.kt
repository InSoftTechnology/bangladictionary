package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {
    @Query("SELECT * FROM search_history ORDER BY searchTime DESC LIMIT 50")
    fun getRecentSearchHistory(): Flow<List<SearchHistory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSearch(history: SearchHistory)

    @Query("DELETE FROM search_history WHERE word = :word")
    suspend fun deleteSearch(word: String)

    @Query("DELETE FROM search_history")
    suspend fun clearHistory()
}
