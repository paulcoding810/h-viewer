package com.paulcoding.hviewer.ui.page.sites.site

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paulcoding.hviewer.H_JS_FUNCTIONS
import com.paulcoding.hviewer.H_JS_PROPERTIES
import com.paulcoding.hviewer.helper.GlobalData
import com.paulcoding.hviewer.helper.SCRIPTS_DIR
import com.paulcoding.hviewer.helper.TabsManager
import com.paulcoding.hviewer.helper.host
import com.paulcoding.hviewer.model.PostItem
import com.paulcoding.hviewer.model.PostsEntity
import com.paulcoding.hviewer.model.toPosts
import com.paulcoding.hviewer.repository.FavoriteRepository
import com.paulcoding.js.JS
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PostsViewModel(
    postUrl: String,
    private val isSearch: Boolean,
    val tabsManager: TabsManager,
    private val favoriteRepository: FavoriteRepository,
) : ViewModel() {
    private var currentPage: String = postUrl
    private var nextPage: String? = null

    private val siteConfig = GlobalData.siteConfigMap[postUrl.host]!!

    private val js = JS(
        fileRelativePath = SCRIPTS_DIR + "/${siteConfig.scriptFile}",
        properties = mapOf(H_JS_PROPERTIES.BASE_URL to siteConfig.baseUrl)
    )

    private var _stateFlow = MutableStateFlow(UiState())
    val stateFlow = _stateFlow.asStateFlow()

    private val _postItem = MutableStateFlow<List<PostItem>>(emptyList())

    val postItems = combine(_postItem, favoriteRepository.favoritePostUrls) { posts, favorites ->
        posts.map { post ->
            post.copy(favorite = favorites.contains(post.url))
        }
    }

    fun setQueryAndSearch(query: String) {
        viewModelScope.launch {
            _postItem.value = emptyList()

            js.callFunction<String>(H_JS_FUNCTIONS.GET_SEARCH_URL, arrayOf(query))
                .onSuccess { queryUrl ->
                    currentPage = queryUrl
                    getPosts(1)
                }
                .onFailure { setError(it) }
        }
    }

    private fun setError(th: Throwable) {
        th.printStackTrace()
        _stateFlow.update { it.copy(error = th) }
    }

    private fun launchAndLoad(block: suspend () -> Unit) {
        viewModelScope.launch {
            _stateFlow.update { it.copy(isLoading = true) }
            block()
            _stateFlow.update { it.copy(isLoading = false) }
        }
    }

    fun getPosts(page: Int) {
        val url = if (page == 1) currentPage else nextPage
            ?: return setError(
                Exception("Next page null")
            )
        launchAndLoad {
            val functionName = if (isSearch) H_JS_FUNCTIONS.SEARCH else H_JS_FUNCTIONS.GET_POSTS
            js.callFunction<PostsEntity>(functionName, arrayOf(url, page))
                .onSuccess { postsEntity ->
                    val postsData = postsEntity.toPosts()
                    nextPage = postsData.next
                    _stateFlow.update {
                        it.copy(
                            postsTotalPage = postsData.total,
                        )
                    }
                    _postItem.update { it + postsData.posts }
                }
                .onFailure {
                    setError(it)
                }
        }
    }

    fun getNextPosts() {
        val newPage = _stateFlow.value.postsPage + 1
        _stateFlow.update { it.copy(postsPage = newPage) }
        getPosts(newPage)
    }

    fun canLoadMorePostsData(): Boolean {
        return _stateFlow.value.postsPage < _stateFlow.value.postsTotalPage
    }

    fun toggleFavorite(post: PostItem) {
        viewModelScope.launch {
            favoriteRepository.toggleFavorite(post)
        }
    }

    data class UiState(
        val postsPage: Int = 1,
        val postsTotalPage: Int = 1,
        val isLoading: Boolean = false,
        val error: Throwable? = null,
    )
}