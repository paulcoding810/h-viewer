package com.paulcoding.hviewer.ui.page

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paulcoding.hviewer.BuildConfig
import com.paulcoding.hviewer.MainApp.Companion.appContext
import com.paulcoding.hviewer.database.DatabaseProvider
import com.paulcoding.hviewer.helper.crashLogDir
import com.paulcoding.hviewer.helper.readConfigFile
import com.paulcoding.hviewer.helper.scriptsDir
import com.paulcoding.hviewer.model.PostItem
import com.paulcoding.hviewer.model.SiteConfig
import com.paulcoding.hviewer.model.SiteConfigs
import com.paulcoding.hviewer.network.Github
import com.paulcoding.hviewer.network.SiteConfigsState
import com.paulcoding.hviewer.preference.Preferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File

class AppViewModel : ViewModel() {
    val listScriptFiles: List<File>
        get() = appContext.scriptsDir.listFiles()
            ?.toList()
            ?.filter { it.extension == "json" || it.extension == "js" }
            ?.sortedBy { it.name }
            ?: listOf()

    val listCrashLogFiles: List<File>
        get() = appContext.crashLogDir.listFiles()?.filter { it.isFile }
            ?.sortedByDescending { it.lastModified() } ?: listOf()

    private var _stateFlow = MutableStateFlow(UiState())
    val stateFlow = _stateFlow.asStateFlow()

    val hostsMap = _stateFlow.map { it.siteConfigs?.toHostsMap() ?: mapOf() }
        .stateIn(viewModelScope, SharingStarted.Eagerly, mapOf())

    private var _tabs = MutableStateFlow(listOf<PostItem>())
    val tabs = _tabs.asStateFlow()

    private var _isLocked = MutableStateFlow(Preferences.pin.isNotEmpty())
    val isLocked = _isLocked.asStateFlow()

    val favoritePosts = DatabaseProvider.getInstance().postItemDao().getFavoritePosts()
    val favoriteSet = favoritePosts.map { it.map { post -> post.url }.toSet() }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptySet())
    val historyPosts = DatabaseProvider.getInstance().postItemDao().getViewedPosts()

    val postFavorite =
        { url: String -> DatabaseProvider.getInstance().postItemDao().isFavorite(url) }

    fun setCurrentPost(post: PostItem) {
        _stateFlow.update { it.copy(post = post) }
    }

    data class UiState(
        val post: PostItem = PostItem(),
        val url: String = "",
        val isDevMode: Boolean = BuildConfig.DEBUG,
        val siteConfigs: SiteConfigs? = appContext.readConfigFile<SiteConfigs>().getOrNull(),
        val error: Throwable? = null,
        val checkingForUpdateScripts: Boolean = false,
        val updatingApk: Boolean = false,
    )

    private fun setError(throwable: Throwable) {
        throwable.printStackTrace()
        _stateFlow.update { it.copy(error = throwable) }
    }

    fun setWebViewUrl(url: String) {
        _stateFlow.update { it.copy(url = url) }
    }

    fun getWebViewUrl() = _stateFlow.value.url

    fun setDevMode(isDevMode: Boolean) {
        _stateFlow.update { it.copy(isDevMode = isDevMode) }
    }

    fun addFavorite(postItem: PostItem, reAdded: Boolean = false) {
        viewModelScope.launch {
            DatabaseProvider.getInstance().postItemDao().setFavorite(
                postItem.url,
                favorite = true,
                favoriteAt = if (!reAdded) System.currentTimeMillis() else postItem.favoriteAt,
            )
        }
    }

    fun deleteFavorite(postItem: PostItem) {
        viewModelScope.launch {
            DatabaseProvider.getInstance().postItemDao().setFavorite(
                postItem.url, false
            )
        }
    }

    fun addHistory(postItem: PostItem) {
        viewModelScope.launch {
            DatabaseProvider.getInstance().postItemDao()
                .setViewed(postItem.url, viewed = true)
        }
    }

    fun deleteHistory(postItem: PostItem) {
        viewModelScope.launch {
            DatabaseProvider.getInstance().postItemDao()
                .setViewed(postItem.url, false)
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            DatabaseProvider.getInstance().postItemDao()
                .clearHistory()
        }
    }

    fun addTab(postItem: PostItem) {
        if (!_tabs.value.contains(postItem))
            _tabs.update {
                it + postItem
            }

        // mark as viewed
        viewModelScope.launch {
            DatabaseProvider.getInstance().postItemDao()
                .setViewed(postItem.url)
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
        return hostsMap.value[_stateFlow.value.post.getHost()]
            ?: throw (Exception("No site config found for ${stateFlow.value.post.url}"))
    }

    fun unlock() {
        _isLocked.update { false }
    }

    fun lock() {
        _isLocked.update { true }
    }

    fun refreshLocalConfigs() {
        appContext.readConfigFile<SiteConfigs>()
            .onSuccess { configs ->
                _stateFlow.update { it.copy(siteConfigs = configs) }
            }
            .onFailure { setError(it) }
    }

    fun checkVersionOrUpdate(remoteUrl: String, onUpdate: (SiteConfigsState) -> Unit = {}) {
        viewModelScope.launch {
            _stateFlow.update { it.copy(checkingForUpdateScripts = true) }
            try {
                val result = Github.checkVersionOrUpdate(remoteUrl)
                if (result is SiteConfigsState.NewConfigsInstall || result is SiteConfigsState.Updated) {
                    refreshLocalConfigs()
                }
                onUpdate(result)
            } catch (e: Exception) {
                e.printStackTrace()
                setError(e)
            } finally {
                _stateFlow.update { it.copy(checkingForUpdateScripts = false) }
            }
        }
    }

    fun checkForUpdate(currentVersion: String, onUpdateAvailable: (String, String) -> Unit) {
        viewModelScope.launch {
            _stateFlow.update { it.copy(updatingApk = true) }
            Github.checkForUpdate(currentVersion, onUpdateAvailable)
            _stateFlow.update { it.copy(updatingApk = false) }
        }
    }

    fun downloadAndInstallApk(context: Context, downloadUrl: String) {
        viewModelScope.launch {
            _stateFlow.update { it.copy(updatingApk = true) }
            Github.downloadApk(context, downloadUrl) { file ->
                val uri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    file
                )
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(uri, "application/vnd.android.package-archive")
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
                }
                context.startActivity(intent)
            }
            _stateFlow.update { it.copy(updatingApk = false) }
        }
    }
}