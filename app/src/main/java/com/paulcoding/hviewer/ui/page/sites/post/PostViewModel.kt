package com.paulcoding.hviewer.ui.page.sites.post

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paulcoding.hviewer.helper.GlobalData
import com.paulcoding.hviewer.helper.SCRIPTS_DIR
import com.paulcoding.hviewer.helper.host
import com.paulcoding.hviewer.model.PostData
import com.paulcoding.hviewer.model.PostItem
import com.paulcoding.hviewer.repository.FavoriteRepository
import com.paulcoding.hviewer.repository.HistoryRepository
import com.paulcoding.js.JS
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PostViewModel(
    private val postItem: PostItem,
    private val historyRepository: HistoryRepository,
    private val favoriteRepository: FavoriteRepository,
) : ViewModel() {
    private var _stateFlow = MutableStateFlow(UiState(postItem = postItem))
    val stateFlow = _stateFlow.asStateFlow()

    private val postUrl = postItem.url
    var getImagesAtLaunch = false

    private val siteConfig = GlobalData.siteConfigMap[postUrl.host]!!
    private var js = JS(
        fileRelativePath = SCRIPTS_DIR + "/${siteConfig.scriptFile}",
        properties = mapOf("baseUrl" to siteConfig.baseUrl)
    )

    init {
        viewModelScope.launch {
            historyRepository.insert(postItem)
        }
    }

    fun updateScrollIndex(scrollIndex: Int, scrollOffset: Int) {
        _stateFlow.update { it.copy(scrollIndex = scrollIndex, scrollOffset = scrollOffset) }
    }

    fun toggleFavorite() {
        viewModelScope.launch {
            val isFavorite = !_stateFlow.value.postItem.favorite
            favoriteRepository.toggleFavorite(postItem)
            _stateFlow.update { state -> state.copy(postItem = state.postItem.copy(favorite = isFavorite)) }
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

    fun getImages(page: Int = 1) {
        val url = if (page == 1) postUrl else _stateFlow.value.nextPage
        if (url.isNullOrEmpty())
            return setError(
                Exception("Next page is NULL")
            )
        launchAndLoad {
            js.callFunction<PostData>("getImages", arrayOf(url, page))
                .onSuccess { postData ->
                    getImagesAtLaunch = true

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

    data class UiState(
        val postItem: PostItem,
        val images: Set<String> = setOf(),
        val postPage: Int = 1,
        val postTotalPage: Int = 1,
        val nextPage: String? = null,
        val isLoading: Boolean = false,
        val error: Throwable? = null,
        val currentPostUrl: String = "",
        val isSystemBarHidden: Boolean = false,
        val scrollIndex: Int = 0,
        val scrollOffset: Int = 0,
    )
}
