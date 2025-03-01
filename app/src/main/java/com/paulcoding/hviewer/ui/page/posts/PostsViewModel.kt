package com.paulcoding.hviewer.ui.page.posts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.paulcoding.hviewer.database.DatabaseProvider
import com.paulcoding.hviewer.helper.SCRIPTS_DIR
import com.paulcoding.hviewer.model.PostItem
import com.paulcoding.hviewer.model.Posts
import com.paulcoding.hviewer.model.SiteConfig
import com.paulcoding.js.JS
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PostsViewModel(
    siteConfig: SiteConfig,
    postUrl: String,
    private val isSearch: Boolean = false
) : ViewModel() {
    private var _stateFlow = MutableStateFlow(UiState().copy(currentPage = postUrl))
    val stateFlow = _stateFlow.asStateFlow()

    private val js = JS(
        fileRelativePath = SCRIPTS_DIR + "/${siteConfig.scriptFile}",
        properties = mapOf("baseUrl" to siteConfig.baseUrl)
    )

    data class UiState(
        val postItems: List<PostItem> = listOf(),
        val postsPage: Int = 1,
        val postsTotalPage: Int = 1,
        val currentPage: String = "",
        val nextPage: String? = null,
        val isLoading: Boolean = false,
        val error: Throwable? = null,
    )

    fun setQueryAndSearch(query: String) {
        viewModelScope.launch {
            js.callFunction<String>("getSearchUrl", arrayOf(query))
                .onSuccess { queryUrl ->
                    _stateFlow.update { UiState(currentPage = queryUrl) }
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
        val url = if (page == 1) _stateFlow.value.currentPage else _stateFlow.value.nextPage
            ?: return setError(
                Exception("Next page null")
            )
        launchAndLoad {
            val functionName = if (isSearch) "search" else "getPosts"
            js.callFunction<Posts>(functionName, arrayOf(url, page))
                .onSuccess { postsData ->
                    _stateFlow.update {
                        it.copy(
                            postItems = it.postItems + postsData.posts,
                            postsTotalPage = postsData.total,
                            nextPage = postsData.next
                        )
                    }
                    // store all posts to database.
                    DatabaseProvider.getInstance().postItemDao().addPosts(postsData.posts)
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
}

@Suppress("UNCHECKED_CAST")
class PostsViewModelFactory(
    private val siteConfig: SiteConfig,
    private val postUrl: String,
    private val isSearch: Boolean = false,
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PostsViewModel::class.java)) {
            return PostsViewModel(siteConfig, postUrl, isSearch) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}