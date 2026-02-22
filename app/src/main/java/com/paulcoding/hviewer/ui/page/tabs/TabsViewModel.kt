package com.paulcoding.hviewer.ui.page.tabs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paulcoding.hviewer.model.PostItem
import com.paulcoding.hviewer.repository.FavoriteRepository
import com.paulcoding.hviewer.repository.TabsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TabsViewModel(
    private val tabsRepository: TabsRepository,
    private val favoriteRepository: FavoriteRepository,
) :
    ViewModel() {

    val tabsWithFavorite: StateFlow<List<PostItem>> =
        combine(tabsRepository.tabs, favoriteRepository.favoritePostUrls) { tabs, favorites ->
            tabs.map { tab ->
                val isFavorite = favorites.contains(tab.url)
                tab.copy(favorite = isFavorite)
            }.reversed()
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


    fun toggleFavorite(postItem: PostItem) {
        viewModelScope.launch {
            if (postItem.favorite)
                favoriteRepository.deleteFavorite(postItem)
            else
                favoriteRepository.addFavorite(postItem)
        }
    }

    fun removeTab(postItem: PostItem) {
        tabsRepository.removeTab(postItem)
    }

    fun clearTabs() {
        tabsRepository.clearTabs()
    }
}