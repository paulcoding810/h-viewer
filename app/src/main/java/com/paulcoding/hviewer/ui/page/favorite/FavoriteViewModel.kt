package com.paulcoding.hviewer.ui.page.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paulcoding.hviewer.helper.TabsManager
import com.paulcoding.hviewer.model.FavoriteEntity
import com.paulcoding.hviewer.model.PostItem
import com.paulcoding.hviewer.model.toPostItem
import com.paulcoding.hviewer.repository.FavoriteRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FavoriteViewModel(private val favoriteRepository: FavoriteRepository, val tabsManager: TabsManager) :
    ViewModel() {
    var lastDeletedItem: PostItem? = null

    val favoritePosts = favoriteRepository.favoritePosts.map { it.map(FavoriteEntity::toPostItem) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun deleteFavorite(postItem: PostItem) {
        viewModelScope.launch {
            lastDeletedItem = postItem
            favoriteRepository.deleteFavorite(postItem)
        }
    }

    fun undoDelete() {
        lastDeletedItem?.let {
            viewModelScope.launch {
                favoriteRepository.addFavorite(postItem = it, keepTimestamp = true)
            }
        }
        lastDeletedItem = null
    }
}
