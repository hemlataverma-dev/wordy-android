package com.hemlata.wordy.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [WordEntity::class, HistoryEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun wordDao(): WordDao
}