package com.hemlata.wordy.domain.repository

import androidx.lifecycle.LiveData
import com.hemlata.wordy.core.utils.Resource
import com.hemlata.wordy.data.local.HistoryEntity
import com.hemlata.wordy.data.local.WordEntity
import com.hemlata.wordy.data.model.WordResponse

interface DictionaryRepository {
    suspend fun getWordMeaning(word: String): Resource<List<WordResponse>>
    suspend fun addFavourite(word: WordEntity)
    suspend fun removeFavourite(word: WordEntity)
    fun getAllFavourites(): LiveData<List<WordEntity>>
    fun getHistory(): LiveData<List<HistoryEntity>>
    suspend fun clearHistory()
    suspend fun isFavourite(word: String): Boolean
}