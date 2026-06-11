package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dictionary_words")
data class DictionaryWord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val word: String,                    // The search word (e.g., "dictionary" or "অভিধান")
    val translation: String,             // The translation (e.g., "অভিধান" or "dictionary")
    val pronunciation: String = "",       // Phonetic/English pronunciation (e.g., "ovidhan")
    val partOfSpeech: String = "",       // e.g. "Noun", "Verb", "Noun (বিশেষ্য)"
    val definition: String = "",         // Comprehensive definition
    val exampleSentence: String = "",    // Custom example sentence
    val exampleTranslation: String = "", // Translation of the example sentence
    val synonyms: String = "",           // Comma-separated synonyms
    val antonyms: String = "",           // Comma-separated antonyms
    val language: String,                // "en_to_bn" or "bn_to_en"
    val isApproved: Boolean = true,      // Word approval system
    val isFavorite: Boolean = false,     // Favorites/Bookmarks
    val searchCount: Int = 0            // Popularity / Analytics
)
