package com.paulcoding.hviewer.ui.page.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paulcoding.hviewer.helper.TabsManager
import com.paulcoding.hviewer.model.FavoriteEntity
import com.paulcoding.hviewer.model.PostItem
import com.paulcoding.hviewer.model.toPostItem
import com.paulcoding.hviewer.repository.FavoriteRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FavoriteViewModel(private val favoriteRepository: FavoriteRepository, val tabsManager: TabsManager) :
    ViewModel() {
    private var lastDeletedItem: PostItem? = null

    private val _query = MutableStateFlow("")
    val query = _query.asStateFlow()

    private val favoritePosts = favoriteRepository.favoritePosts.map { it.map(FavoriteEntity::toPostItem) }

    @OptIn(FlowPreview::class)
    val favoritePostsWithQuery = combine(favoritePosts, query.debounce { 400 }) { posts, query ->
        posts.filter { post ->
            query.isEmpty() || post.name.contains(query, ignoreCase = true)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun onAction(action: Action) {
        when (action) {
            is Action.QueryChanged -> _query.value = action.query
            is Action.Delete -> deleteFavorite(action.postItem)
            is Action.UndoDelete -> undoDelete()
        }
    }

    private fun deleteFavorite(postItem: PostItem) {
        viewModelScope.launch {
            lastDeletedItem = postItem
            favoriteRepository.deleteFavorite(postItem)
        }
    }

    private fun undoDelete() {
        lastDeletedItem?.let {
            viewModelScope.launch {
                favoriteRepository.addFavorite(postItem = it, keepTimestamp = true)
            }
        }
        lastDeletedItem = null
    }

    sealed class Action {
        object UndoDelete : Action()
        data class Delete(val postItem: PostItem) : Action()
        data class QueryChanged(val query: String) : Action()
    }
}
