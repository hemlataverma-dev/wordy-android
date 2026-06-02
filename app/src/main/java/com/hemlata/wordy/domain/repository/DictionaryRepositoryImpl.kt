package com.hemlata.wordy.data.repository

import com.hemlata.wordy.core.utils.Resource
import com.hemlata.wordy.data.local.HistoryEntity
import com.hemlata.wordy.data.local.WordDao
import com.hemlata.wordy.data.model.WordResponse
import com.hemlata.wordy.data.remote.DictionaryApi
import com.hemlata.wordy.domain.repository.DictionaryRepository
import javax.inject.Inject

class DictionaryRepositoryImpl @Inject constructor(
    private val api: DictionaryApi,
    private val wordDao: WordDao
) : DictionaryRepository {

    override suspend fun getWordMeaning(word: String): Resource<List<WordResponse>> {
        return try {
            val response = api.getWordMeaning(word)
            wordDao.addToHistory(HistoryEntity(word = word))
            Resource.Success(response)
        } catch (e: retrofit2.HttpException) {
            if (e.code() == 404) {
                Resource.Error("Word not found. Try another word.")
            } else {
                Resource.Error("Something went wrong. Please try again.")
            }
        } catch (e: java.io.IOException) {
            Resource.Error("No internet connection. Check your network.")
        } catch (e: Exception) {
            Resource.Error("Unexpected error: ${e.localizedMessage}")
        }
    }

    override suspend fun addFavourite(word: com.hemlata.wordy.data.local.WordEntity) {
        wordDao.addFavourite(word)
    }

    override suspend fun removeFavourite(word: com.hemlata.wordy.data.local.WordEntity) {
        wordDao.removeFavourite(word)
    }

    override fun getAllFavourites() = wordDao.getAllFavourites()

    override fun getHistory() = wordDao.getHistory()

    override suspend fun clearHistory() = wordDao.clearHistory()

    override suspend fun isFavourite(word: String) = wordDao.getFavouriteByWord(word) != null
}