package com.hemlata.wordy.core.network

import com.hemlata.wordy.data.remote.DictionaryApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideRetrofit(): retrofit2.Retrofit {
        return RetrofitClient.instance
    }

    @Provides
    @Singleton
    fun provideDictionaryApi(retrofit: retrofit2.Retrofit): DictionaryApi {
        return retrofit.create(DictionaryApi::class.java)
    }
}