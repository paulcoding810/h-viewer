package com.paulcoding.hviewer.ui.page.tabs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paulcoding.hviewer.model.PostItem
import com.paulcoding.hviewer.repository.FavoriteRepository
import com.paulcoding.hviewer.ui.page.sites.post.PostImagesDelegate

class TabViewModel(
    private val postItem: PostItem,
    private val favoriteRepository: FavoriteRepository
) : ViewModel() {
    val delegate = PostImagesDelegate(
        viewModelScope = viewModelScope,
        postItem = postItem,
        favoriteRepository = favoriteRepository,
    )
}