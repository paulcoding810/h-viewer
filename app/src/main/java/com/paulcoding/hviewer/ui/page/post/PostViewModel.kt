package com.paulcoding.hviewer.ui.page.post

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.paulcoding.hviewer.js.JS
import com.paulcoding.hviewer.model.PostData
import com.paulcoding.hviewer.model.SiteConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PostViewModel(private val postUrl: String, siteConfig: SiteConfig) : ViewModel() {
    private var _stateFlow = MutableStateFlow(UiState())
    val stateFlow = _stateFlow.asStateFlow()

    private val js = JS(siteConfig.scriptFile)

    data class UiState(
        val images: List<String> = listOf(),
        val postPage: Int = 1,
        val postTotalPage: Int = 1,
        val isLoading: Boolean = false,
        val error: Throwable? = null,
        val currentPostUrl: String = "",
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

    fun getImages(page: Int = 1) {
        launchAndLoad {
            js.callFunction<PostData>("getImages", arrayOf(postUrl, page))
                .onSuccess { postData ->
                    _stateFlow.update {
                        it.copy(
                            isLoading = false,
                            images = it.images + postData.images,
                            postTotalPage = postData.total
                        )
                    }
                }
                .onFailure {
                    setError(it)
                }
        }
    }


    fun getNextImages() {
        val newPage = _stateFlow.value.postPage + 1
        _stateFlow.update { it.copy(postPage = newPage) }
        getImages(newPage)
    }

    fun canLoadMorePostData(): Boolean {
        return _stateFlow.value.postPage < _stateFlow.value.postTotalPage
    }
}

@Suppress("UNCHECKED_CAST")
class PostViewModelFactory(private val postUrl: String, private val siteConfig: SiteConfig) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PostViewModel::class.java)) {
            return PostViewModel(postUrl, siteConfig) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
