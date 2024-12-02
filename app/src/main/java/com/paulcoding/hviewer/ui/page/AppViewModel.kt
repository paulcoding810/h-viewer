package com.paulcoding.hviewer.ui.page

import androidx.lifecycle.ViewModel
import com.paulcoding.hviewer.model.PostItem
import com.paulcoding.hviewer.model.SiteConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AppViewModel : ViewModel() {
    private var _stateFlow = MutableStateFlow(UiState())
    val stateFlow = _stateFlow.asStateFlow()

    fun setCurrentPost(post: PostItem) {
        _stateFlow.update { it.copy(post = post) }
    }

    fun setSiteConfig(siteConfig: SiteConfig) {
        _stateFlow.update { it.copy(siteConfig = siteConfig) }
    }

    data class UiState(
        val post: PostItem = PostItem(),
        val siteConfig: SiteConfig = SiteConfig(),
    )
}