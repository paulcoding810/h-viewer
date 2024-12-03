package com.paulcoding.hviewer.ui.page

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paulcoding.hviewer.database.DatabaseProvider
import com.paulcoding.hviewer.model.PostItem
import com.paulcoding.hviewer.model.SiteConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AppViewModel : ViewModel() {
    private var _stateFlow = MutableStateFlow(UiState())
    val stateFlow = _stateFlow.asStateFlow()

    val favoritePosts = DatabaseProvider.getInstance().favoritePostDao().getAll()

    fun setCurrentPost(post: PostItem) {
        _stateFlow.update { it.copy(post = post) }
    }

    fun setSiteConfig(site: String, siteConfig: SiteConfig) {
        _stateFlow.update { it.copy(site = site to siteConfig) }
    }

    data class UiState(
        val post: PostItem = PostItem(),
        val site: Pair<String, SiteConfig> = "" to SiteConfig(),
    )

    fun addFavorite(postItem: PostItem) {
        viewModelScope.launch {
            val postWithPage = postItem.copy(site = _stateFlow.value.site.first)
            DatabaseProvider.getInstance().favoritePostDao().insert(postWithPage)
        }
    }

    fun deleteFavorite(postItem: PostItem) {
        viewModelScope.launch {
            DatabaseProvider.getInstance().favoritePostDao().delete(postItem)
        }
    }
}