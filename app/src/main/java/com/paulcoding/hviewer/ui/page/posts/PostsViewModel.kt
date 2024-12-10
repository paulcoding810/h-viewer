package com.paulcoding.hviewer.ui.page.posts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.paulcoding.hviewer.js.JS
import com.paulcoding.hviewer.model.PostItem
import com.paulcoding.hviewer.model.Posts
import com.paulcoding.hviewer.model.SiteConfig
import com.paulcoding.hviewer.model.Tag
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PostsViewModel(siteConfig: SiteConfig, tag: Tag) : ViewModel() {
    private val topicUrl = tag.url
    private var _stateFlow = MutableStateFlow(UiState())
    val stateFlow = _stateFlow.asStateFlow()

    private val js = JS(siteConfig)

    data class UiState(
        val postItems: List<PostItem> = listOf(),
        val postsPage: Int = 1,
        val postsTotalPage: Int = 1,
        val nextPage: String? = null,
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

    fun getPosts(page: Int) {
        val url = if (page == 1) topicUrl else _stateFlow.value.nextPage ?: return setError(
            Exception("Next page null")
        )
        launchAndLoad {
            js.callFunction<Posts>("getPosts", arrayOf(url, page))
                .onSuccess { postsData ->
                    _stateFlow.update {
                        it.copy(
                            postItems = it.postItems + postsData.posts,
                            postsTotalPage = postsData.total,
                            nextPage = postsData.next
                        )
                    }
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
class PostsViewModelFactory(private val siteConfig: SiteConfig, private val tag: Tag) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PostsViewModel::class.java)) {
            return PostsViewModel(siteConfig, tag) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}