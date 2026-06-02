package com.hemlata.wordy.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface WordDao {

    // Favourites
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavourite(word: WordEntity)

    @Delete
    suspend fun removeFavourite(word: WordEntity)

    @Query("SELECT * FROM favourites ORDER BY savedAt DESC")
    fun getAllFavourites(): LiveData<List<WordEntity>>

    @Query("SELECT * FROM favourites WHERE word = :word")
    suspend fun getFavouriteByWord(word: String): WordEntity?

    // History
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addToHistory(history: HistoryEntity)

    @Query("SELECT * FROM history ORDER BY searchedAt DESC LIMIT 20")
    fun getHistory(): LiveData<List<HistoryEntity>>

    @Query("DELETE FROM history")
    suspend fun clearHistory()
}