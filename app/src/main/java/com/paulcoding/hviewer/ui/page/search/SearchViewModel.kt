package com.paulcoding.hviewer.ui.page.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.paulcoding.hviewer.helper.SCRIPTS_DIR
import com.paulcoding.hviewer.model.PostItem
import com.paulcoding.hviewer.model.Posts
import com.paulcoding.hviewer.model.SiteConfig
import com.paulcoding.js.JS
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SearchViewModel(siteConfig: SiteConfig) : ViewModel() {
    private var _stateFlow = MutableStateFlow(UiState())
    val stateFlow = _stateFlow.asStateFlow()

    private val js = JS(
        fileRelativePath = SCRIPTS_DIR + "/${siteConfig.scriptFile}",
        properties = mapOf("baseUrl" to siteConfig.baseUrl)
    )

    data class UiState(
        val postItems: List<PostItem> = listOf(),
        val query: String = "",
        val postsPage: Int = 1,
        val postsTotalPage: Int = 1,
        val queryUrl: String? = null,
        val isLoading: Boolean = false,
        val error: Throwable? = null,
    )

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

    fun setQueryAndSearch(query: String) {
        viewModelScope.launch {
            js.callFunction<String>("getSearchUrl", arrayOf(query))
                .onSuccess { queryUrl ->
                    _stateFlow.update { UiState(query = query, queryUrl = queryUrl) }
                    getPosts(1)
                }
                .onFailure { setError(it) }
        }
    }

     fun getPosts(page: Int) {
        val url = _stateFlow.value.queryUrl ?: return setError(Exception("Next page null"))
        launchAndLoad {
            js.callFunction<Posts>("search", arrayOf(url, page))
                .onSuccess { postsData ->
                    _stateFlow.update {
                        it.copy(
                            postItems = it.postItems + postsData.posts,
                            postsTotalPage = postsData.total,
                            queryUrl = postsData.next
                        )
                    }
                }
                .onFailure {
                    setError(it)
                }
        }
    }

    fun getNextPosts() {
        launchAndLoad {
            val newPage = _stateFlow.value.postsPage + 1
            _stateFlow.update { it.copy(postsPage = newPage) }
            getPosts(newPage)
        }
    }

    fun canLoadMorePostsData(): Boolean {
        return _stateFlow.value.postsPage < _stateFlow.value.postsTotalPage
    }
}

@Suppress("UNCHECKED_CAST")
class SearchViewModelFactory(private val siteConfig: SiteConfig) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
            return SearchViewModel(siteConfig) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}