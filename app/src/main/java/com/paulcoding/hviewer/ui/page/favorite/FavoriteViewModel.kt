package com.paulcoding.hviewer.ui.page.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paulcoding.hviewer.helper.TabsManager
import com.paulcoding.hviewer.model.PostItem
import com.paulcoding.hviewer.repository.FavoriteRepository
import kotlinx.coroutines.launch

class FavoriteViewModel(private val favoriteRepository: FavoriteRepository, val tabsManager: TabsManager) :
    ViewModel() {
    val favoritePosts = favoriteRepository.favoritePosts

    fun addFavorite(postItem: PostItem, reAdded: Boolean = false) {
        viewModelScope.launch {
            favoriteRepository.addFavorite(postItem, reAdded)
        }
    }

    fun deleteFavorite(postItem: PostItem) {
        viewModelScope.launch {
            favoriteRepository.deleteFavorite(postItem)
        }
    }
}
