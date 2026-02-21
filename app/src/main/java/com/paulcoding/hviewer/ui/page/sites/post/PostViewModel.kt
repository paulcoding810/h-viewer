package com.paulcoding.hviewer.ui.page.sites.post

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paulcoding.hviewer.repository.FavoriteRepository
import com.paulcoding.hviewer.repository.HistoryRepository
import com.paulcoding.hviewer.ui.page.Routes
import kotlinx.coroutines.launch

class PostViewModel(
    savedStateHandle: SavedStateHandle,
    private val historyRepository: HistoryRepository,
    private val favoriteRepository: FavoriteRepository
) : ViewModel() {
    val arguments = Routes.Post.from(savedStateHandle)
    val postItem = arguments.postItem

    init {
        viewModelScope.launch {
            historyRepository.setViewed(postItem, true)
        }
    }

    val delegate = PostImagesDelegate(
        viewModelScope = viewModelScope,
        postItem = postItem,
        favoriteRepository = favoriteRepository,
    )
}