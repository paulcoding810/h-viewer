package com.paulcoding.hviewer.ui.page

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paulcoding.hviewer.BuildConfig
import com.paulcoding.hviewer.MainApp.Companion.appContext
import com.paulcoding.hviewer.database.DatabaseProvider
import com.paulcoding.hviewer.helper.readConfigFile
import com.paulcoding.hviewer.model.PostItem
import com.paulcoding.hviewer.model.SiteConfig
import com.paulcoding.hviewer.model.SiteConfigs
import com.paulcoding.hviewer.repository.UpdateAppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File

class AppViewModel(
    private val updateAppRepository: UpdateAppRepository
) : ViewModel() {


    private var _stateFlow = MutableStateFlow(UiState())
    val stateFlow = _stateFlow.asStateFlow()




    fun setCurrentPost(post: PostItem) {
        _stateFlow.update { it.copy(post = post) }
    }

    data class UiState(
        val post: PostItem = PostItem(),
        val url: String = "",
        val isDevMode: Boolean = BuildConfig.DEBUG,
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


    //fun getCurrentSiteConfig(): SiteConfig {
    //    return hostsMap.value[_stateFlow.value.post.getHost()]
    //        ?: throw (Exception("No site config found for ${stateFlow.value.post.url}"))
    //}


    //
    //
    //fun checkForUpdate(
    //    currentVersion: String,
    //    onUpToDate: () -> Unit,
    //    onUpdateAvailable: (String, String) -> Unit
    //) {
    //    viewModelScope.launch {
    //        _stateFlow.update { it.copy(updatingApk = true) }
    //        val release = updateAppRepository.getLatestAppRelease(currentVersion)
    //        if (release != null)
    //            onUpdateAvailable(release.version, release.downloadUrl)
    //        else
    //            onUpToDate()
    //        _stateFlow.update { it.copy(updatingApk = false) }
    //    }
    //}

    //fun downloadAndInstallApk(context: Context, downloadUrl: String) {
    //    viewModelScope.launch {
    //        _stateFlow.update { it.copy(updatingApk = true) }
    //        updateAppRepository.downloadApk(
    //            downloadUrl,
    //            destination = File(context.cacheDir, "latest.apk")
    //        ) { file ->
    //            val uri = FileProvider.getUriForFile(
    //                context,
    //                "${context.packageName}.fileprovider",
    //                file
    //            )
    //            installApk(context, uri)
    //        }
    //        _stateFlow.update { it.copy(updatingApk = false) }
    //    }
    //}

    //fun installApk(context: Context, uri: Uri) {
    //    val intent = Intent(Intent.ACTION_VIEW).apply {
    //        setDataAndType(uri, "application/vnd.android.package-archive")
    //        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
    //    }
    //    context.startActivity(intent)
    //}
}