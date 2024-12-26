package com.paulcoding.hviewer.ui.page

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paulcoding.hviewer.BuildConfig
import com.paulcoding.hviewer.MainApp.Companion.appContext
import com.paulcoding.hviewer.database.DatabaseProvider
import com.paulcoding.hviewer.helper.crashLogDir
import com.paulcoding.hviewer.helper.scriptsDir
import com.paulcoding.hviewer.model.PostHistory
import com.paulcoding.hviewer.model.PostItem
import com.paulcoding.hviewer.model.SiteConfig
import com.paulcoding.hviewer.model.Tag
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
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

    val favoritePosts = DatabaseProvider.getInstance().favoritePostDao().getAll()
    val historyPosts = DatabaseProvider.getInstance().historyDao().getAll()

    fun setCurrentPost(post: PostItem) {
        _stateFlow.update { it.copy(post = post) }
    }

    fun setCurrentTag(tag: Tag) {
        _stateFlow.update { it.copy(tag = tag) }
    }

    fun getCurrentTag() = _stateFlow.value.tag

    fun setSiteConfig(site: String, siteConfig: SiteConfig) {
        _stateFlow.update { it.copy(site = site to siteConfig) }
    }


    data class UiState(
        val post: PostItem = PostItem(),
        val site: Pair<String, SiteConfig> = "" to SiteConfig(),
        val tag: Tag = Tag(),
        val isDevMode: Boolean = BuildConfig.DEBUG,
    )

    fun setDevMode(isDevMode: Boolean) {
        _stateFlow.update { it.copy(isDevMode = isDevMode) }
    }

    fun addFavorite(postItem: PostItem, reAdded: Boolean = false) {
        viewModelScope.launch {
            val item = if (reAdded) postItem else postItem.copy(
                site = _stateFlow.value.site.first,
                createdAt = System.currentTimeMillis()
            )
            DatabaseProvider.getInstance().favoritePostDao().insert(item)
        }
    }

    fun deleteFavorite(postItem: PostItem) {
        viewModelScope.launch {
            DatabaseProvider.getInstance().favoritePostDao().delete(postItem)
        }
    }

    fun addHistory(postItem: PostItem) {
        val item = PostHistory(
            site = _stateFlow.value.site.first,
            createdAt = System.currentTimeMillis(),
            url = postItem.url,
            views = postItem.views,
            thumbnail = postItem.thumbnail,
            name = postItem.name,
            size = postItem.size,
            tags = postItem.tags,
            quantity = postItem.quantity
        )
        viewModelScope.launch {
            // limit to 25 items in history
            if (historyPosts.first().size >= 25) {
                DatabaseProvider.getInstance().historyDao().deleteOldest()
            }
            DatabaseProvider.getInstance().historyDao()
                .insert(item)
        }
    }

    fun deleteHistory(history: PostHistory) {
        viewModelScope.launch {
            DatabaseProvider.getInstance().historyDao().delete(history)
        }
    }
}