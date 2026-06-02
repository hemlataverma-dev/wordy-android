package com.hemlata.wordy.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history")
data class HistoryEntity(
    @PrimaryKey
    val word: String,
    val searchedAt: Long = System.currentTimeMillis()
)