package com.hemlata.wordy.presentation.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hemlata.wordy.core.utils.Resource
import com.hemlata.wordy.data.local.WordEntity
import com.hemlata.wordy.data.model.WordResponse
import com.hemlata.wordy.domain.repository.DictionaryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: DictionaryRepository
) : ViewModel() {

    private val _wordResult = MutableLiveData<Resource<List<WordResponse>>>()
    val wordResult: LiveData<Resource<List<WordResponse>>> = _wordResult

    private val _isFavourite = MutableLiveData<Boolean>()
    val isFavourite: LiveData<Boolean> = _isFavourite

    private var currentWord: WordResponse? = null
    private var searchJob: Job? = null

    fun searchWord(word: String) {
        if (word.trim().isEmpty()) return
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(500)
            _wordResult.value = Resource.Loading()
            val result = repository.getWordMeaning(word.trim())
            if (result is Resource.Success) {
                currentWord = result.data?.firstOrNull()
                checkFavourite(word.trim())
            }
            _wordResult.value = result
        }
    }

    private fun checkFavourite(word: String) {
        viewModelScope.launch {
            _isFavourite.value = repository.isFavourite(word)
        }
    }

    fun toggleFavourite() {
        val word = currentWord ?: return
        viewModelScope.launch {
            val isFav = repository.isFavourite(word.word)
            if (isFav) {
                repository.removeFavourite(
                    WordEntity(
                        word = word.word,
                        phonetic = word.phonetic,
                        definitions = word.meanings
                            .flatMap { it.definitions }
                            .take(3)
                            .joinToString("|") { it.definition },
                        partOfSpeech = word.meanings
                            .firstOrNull()?.partOfSpeech ?: ""
                    )
                )
                _isFavourite.value = false
            } else {
                repository.addFavourite(
                    WordEntity(
                        word = word.word,
                        phonetic = word.phonetic,
                        definitions = word.meanings
                            .flatMap { it.definitions }
                            .take(3)
                            .joinToString("|") { it.definition },
                        partOfSpeech = word.meanings
                            .firstOrNull()?.partOfSpeech ?: ""
                    )
                )
                _isFavourite.value = true
            }
        }
    }

    fun clearResult() {
        _wordResult.value = null
        currentWord = null
        _isFavourite.value = false
    }
}