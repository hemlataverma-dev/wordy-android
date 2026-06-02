package com.hemlata.wordy.data.remote

import com.hemlata.wordy.data.model.WordResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface DictionaryApi {

    @GET("entries/en/{word}")
    suspend fun getWordMeaning(
        @Path("word") word: String
    ): List<WordResponse>
}