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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class FavoriteViewModel(
    private val favoriteRepository: FavoriteRepository,
    val tabsManager: TabsManager,
    private val json: Json
) :
    ViewModel() {
    private var lastDeletedItem: PostItem? = null

    private val _query = MutableStateFlow("")
    val query = _query.asStateFlow()

    private val _effect = MutableStateFlow<Effect?>(null)
    val effect = _effect.asStateFlow()

    private val _importState = MutableStateFlow(ImportState())
    val importState = _importState.asStateFlow()

    private val favoritePosts = favoriteRepository.favoritePosts.map { it.map(FavoriteEntity::toPostItem) }

    @OptIn(FlowPreview::class)
    val favoritePostsWithQuery =
        combine(favoritePosts, query.debounce { 400 }) { posts, query ->
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
            is Action.Export -> export()
            is Action.Import -> importPosts(action.data)
            is Action.ConsumeEffect -> consumeEffect()
        }
    }

    private fun importPosts(data: String) {
        viewModelScope.launch {
            try {
                _importState.update { it.copy(isLoading = true) }
                val posts = json.decodeFromString<List<PostItem>>(data)
                val results = favoriteRepository.addFavorites(posts)

                _importState.update {
                    it.copy(
                        isSuccess = true,
                        successCount = results.count { result -> result != -1L },
                        isLoading = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _importState.update {
                    it.copy(
                        isSuccess = false,
                        successCount = 0,
                        isLoading = false,
                        error = e.message
                    )
                }
            }
        }
    }

    private fun consumeEffect() {
        _effect.value = null
    }

    private fun export() {
        val data = json.encodeToString(favoritePostsWithQuery.value)
        _effect.value = Effect.CopyToClipboard(data)

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

    data class ImportState(
        val isLoading: Boolean = false,
        val isSuccess: Boolean = false,
        val successCount: Int = 0,
        val error: String? = null
    )

    sealed class Effect {
        data class CopyToClipboard(val data: String) : Effect()
    }

    sealed class Action {
        object UndoDelete : Action()
        object Export : Action()
        object ConsumeEffect : Action()
        data class Import(val data: String) : Action()
        data class Delete(val postItem: PostItem) : Action()
        data class QueryChanged(val query: String) : Action()
    }
}
