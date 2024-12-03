package com.paulcoding.hviewer.ui.page

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paulcoding.hviewer.MainApp.Companion.appContext
import com.paulcoding.hviewer.database.DatabaseProvider
import com.paulcoding.hviewer.helper.scriptsDir
import com.paulcoding.hviewer.model.PostItem
import com.paulcoding.hviewer.model.SiteConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File

class AppViewModel : ViewModel() {
    private val scriptsDir = appContext.scriptsDir
    val listScriptFiles: List<File>
        get() = scriptsDir.listFiles()?.toList() ?: listOf()

    private var _stateFlow = MutableStateFlow(UiState())
    val stateFlow = _stateFlow.asStateFlow()

    val favoritePosts = DatabaseProvider.getInstance().favoritePostDao().getAll()

    fun setCurrentPost(post: PostItem) {
        _stateFlow.update { it.copy(post = post) }
    }

    fun setSiteConfig(site: String, siteConfig: SiteConfig) {
        _stateFlow.update { it.copy(site = site to siteConfig) }
    }

    data class UiState(
        val post: PostItem = PostItem(),
        val site: Pair<String, SiteConfig> = "" to SiteConfig(),
    )

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
}