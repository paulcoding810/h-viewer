package com.paulcoding.hviewer.ui.page.post

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.paulcoding.hviewer.helper.SCRIPTS_DIR
import com.paulcoding.hviewer.model.PostData
import com.paulcoding.hviewer.model.SiteConfig
import com.paulcoding.js.JS
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PostViewModel(private val postUrl: String, siteConfig: SiteConfig) : ViewModel() {
    private var _stateFlow = MutableStateFlow(UiState())
    val stateFlow = _stateFlow.asStateFlow()

    private val js = JS(
        fileRelativePath = SCRIPTS_DIR + "/${siteConfig.scriptFile}",
        properties = mapOf("baseUrl" to siteConfig.baseUrl)
    )

    data class UiState(
        val images: List<String> = listOf(),
        val postPage: Int = 1,
        val postTotalPage: Int = 1,
        val nextPage: String? = null,
        val isLoading: Boolean = false,
        val error: Throwable? = null,
        val currentPostUrl: String = "",
        val isSystemBarHidden: Boolean = true,
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
        val url = if (page == 1) postUrl else _stateFlow.value.nextPage
        if (url.isNullOrEmpty())
            return setError(
                Exception("Next page is NULL")
            )
        launchAndLoad {
            js.callFunction<PostData>("getImages", arrayOf(url, page))
                .onSuccess { postData ->
                    _stateFlow.update {
                        it.copy(
                            isLoading = false,
                            images = it.images + postData.images,
                            postTotalPage = postData.total,
                            nextPage = postData.next
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

    fun toggleSystemBarHidden() {
        _stateFlow.update { it.copy(isSystemBarHidden = !it.isSystemBarHidden) }
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
