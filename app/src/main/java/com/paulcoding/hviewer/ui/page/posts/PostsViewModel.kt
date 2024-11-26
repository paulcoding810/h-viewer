package com.paulcoding.hviewer.ui.page.posts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.paulcoding.hviewer.MainApp.Companion.appContext
import com.paulcoding.hviewer.ui.model.SiteConfig
import com.paulcoding.hviewer.js.JS
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PostsViewModel(siteConfig: SiteConfig, topic: String) : ViewModel() {
    private val topicUrl = siteConfig.tags[topic] ?: ""

    private var _stateFlow = MutableStateFlow(UiState())
    val stateFlow = _stateFlow.asStateFlow()

    private val js =
        JS(appContext.filesDir.absolutePath + "/${siteConfig.scriptFile}")


    data class UiState(
        val postItems: List<PostItem> = listOf(),
        val postsPage: Int = 1,
        val postsTotalPage: Int = 1,
        val images: List<String> = listOf(),
        val isLoading: Boolean = false,
        val error: Throwable? = null,
        val currentTopicUrl: String = "",
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
        launchAndLoad {
            js.callFunction<Posts>("getPosts", arrayOf(topicUrl, page))
                .onSuccess { postsData ->
                    _stateFlow.update {
                        it.copy(
                            postItems = it.postItems + postsData.posts,
                            postsTotalPage = postsData.total
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

data class PostItem(
    val name: String = "",
    val url: String = "",
    val thumbnail: String = "",
    val views: String = ""
)

data class Posts(
    val posts: List<PostItem> = listOf(),
    val total: Int = 1,
)

@Suppress("UNCHECKED_CAST")
class PostsViewModelFactory(private val siteConfig: SiteConfig, private val topic: String) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PostsViewModel::class.java)) {
            return PostsViewModel(siteConfig, topic) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}