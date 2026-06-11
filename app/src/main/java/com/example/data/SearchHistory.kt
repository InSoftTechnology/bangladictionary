package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "search_history")
data class SearchHistory(
    @PrimaryKey val word: String,        // Searched text
    val searchTime: Long = System.currentTimeMillis() // Sorted newest first
)
