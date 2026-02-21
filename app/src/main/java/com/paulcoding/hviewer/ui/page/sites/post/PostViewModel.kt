package com.paulcoding.hviewer.ui.page.sites.post

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paulcoding.hviewer.repository.FavoriteRepository
import com.paulcoding.hviewer.ui.page.Routes

class PostViewModel(
    savedStateHandle: SavedStateHandle,
    private val favoriteRepository: FavoriteRepository
) : ViewModel() {
    val arguments = Routes.Post.from(savedStateHandle)
    val postItem = arguments.postItem

    val delegate = PostImagesDelegate(
        viewModelScope = viewModelScope,
        postItem = postItem,
        favoriteRepository = favoriteRepository,
    )
}