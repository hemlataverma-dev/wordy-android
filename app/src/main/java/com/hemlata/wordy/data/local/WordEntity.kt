package com.hemlata.wordy.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favourites")
data class WordEntity(
    @PrimaryKey
    val word: String,
    val phonetic: String?,
    val definitions: String,
    val partOfSpeech: String,
    val savedAt: Long = System.currentTimeMillis()
)