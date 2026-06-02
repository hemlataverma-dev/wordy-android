package com.hemlata.wordy.presentation.favourites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hemlata.wordy.data.local.WordEntity
import com.hemlata.wordy.domain.repository.DictionaryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavouritesViewModel @Inject constructor(
    private val repository: DictionaryRepository
) : ViewModel() {

    val favourites = repository.getAllFavourites()

    fun removeFavourite(word: WordEntity) {
        viewModelScope.launch {
            repository.removeFavourite(word)
        }
    }
}