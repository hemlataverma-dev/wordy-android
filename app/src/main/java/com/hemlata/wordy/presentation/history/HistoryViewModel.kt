package com.hemlata.wordy.presentation.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hemlata.wordy.domain.repository.DictionaryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val repository: DictionaryRepository
) : ViewModel() {

    val history = repository.getHistory()

    fun clearHistory() {
        viewModelScope.launch {
            repository.clearHistory()
        }
    }
}