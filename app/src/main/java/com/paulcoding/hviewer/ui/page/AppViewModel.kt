package com.paulcoding.hviewer.ui.page

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paulcoding.hviewer.BuildConfig
import com.paulcoding.hviewer.MainApp.Companion.appContext
import com.paulcoding.hviewer.database.DatabaseProvider
import com.paulcoding.hviewer.helper.crashLogDir
import com.paulcoding.hviewer.helper.scriptsDir
import com.paulcoding.hviewer.model.PostItem
import com.paulcoding.hviewer.model.SiteConfig
import com.paulcoding.hviewer.model.Tag
import com.paulcoding.hviewer.network.Github
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File

class AppViewModel : ViewModel() {
    val listScriptFiles: List<File>
        get() = appContext.scriptsDir.listFiles()?.toList() ?: listOf()

    val listCrashLogFiles: List<File>
        get() = appContext.crashLogDir.listFiles()?.toList() ?: listOf()

    private var _stateFlow = MutableStateFlow(UiState())
    val stateFlow = _stateFlow.asStateFlow()

    private var _tabs = MutableStateFlow(listOf<PostItem>())
    val tabs = _tabs.asStateFlow()

    val favoritePosts = DatabaseProvider.getInstance().postItemDao().getFavoritePosts()
    val historyPosts = DatabaseProvider.getInstance().postItemDao().getViewedPosts()

    fun setCurrentPost(post: PostItem) {
        _stateFlow.update { it.copy(post = post) }
    }

    fun setCurrentTag(tag: Tag) {
        _stateFlow.update { it.copy(tag = tag) }
    }

    fun getCurrentTag() = _stateFlow.value.tag

    data class UiState(
        val post: PostItem = PostItem(),
        val url: String = "",
        val tag: Tag = Tag(),
        val isDevMode: Boolean = BuildConfig.DEBUG,
    )

    fun setWebViewUrl(url: String) {
        _stateFlow.update { it.copy(url = url) }
    }

    fun getWebViewUrl() = _stateFlow.value.url

    fun setDevMode(isDevMode: Boolean) {
        _stateFlow.update { it.copy(isDevMode = isDevMode) }
    }

    fun addFavorite(postItem: PostItem, reAdded: Boolean = false) {
        viewModelScope.launch {
            val item = postItem.copy(
                favorite = true,
                favoriteAt = if (!reAdded) System.currentTimeMillis() else postItem.favoriteAt,
            )
            DatabaseProvider.getInstance().postItemDao().updatePost(item)
        }
    }

    fun deleteFavorite(postItem: PostItem) {
        viewModelScope.launch {
            DatabaseProvider.getInstance().postItemDao().updatePost(
                postItem.copy(favorite = false)
            )
        }
    }

    fun addHistory(postItem: PostItem) {
        viewModelScope.launch {
            DatabaseProvider.getInstance().postItemDao()
                .updatePost(postItem.copy(viewed = true, viewedAt = System.currentTimeMillis()))
        }
    }

    fun deleteHistory(postItem: PostItem) {
        viewModelScope.launch {
            DatabaseProvider.getInstance().postItemDao()
                .updatePost(postItem.copy(viewed = false))

        }
    }

    fun addTab(postItem: PostItem) {
        if (!_tabs.value.contains(postItem))
            _tabs.update {
                it + postItem
            }
    }

    fun removeTab(postItem: PostItem) {
        _tabs.update {
            it - postItem
        }
    }

    fun clearTabs() {
        _tabs.update {
            emptyList()
        }
    }

    fun getCurrentSiteConfig(): SiteConfig {
        val hostMap = Github.stateFlow.value.siteConfigs?.toHostsMap()
        if (hostMap != null) {
            return _stateFlow.value.post.getSiteConfig(hostMap) ?: SiteConfig()
        }
        return SiteConfig()
    }
}