package com.paulcoding.hviewer.ui.page.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paulcoding.hviewer.model.HistoryEntity
import com.paulcoding.hviewer.model.PostItem
import com.paulcoding.hviewer.model.toPostItem
import com.paulcoding.hviewer.repository.HistoryRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HistoryViewModel(private val historyRepository: HistoryRepository) : ViewModel() {
    val viewedPosts = historyRepository.viewedPosts.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun deleteHistory(item: HistoryEntity) {
        viewModelScope.launch {
            historyRepository.delete(item)
        }
    }

    fun deleteAllHistory() {
        viewModelScope.launch {
            historyRepository.deleteAll()
        }
    }
}