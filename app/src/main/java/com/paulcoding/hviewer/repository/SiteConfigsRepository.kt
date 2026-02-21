package com.paulcoding.hviewer.repository

import android.content.Context
import com.paulcoding.hviewer.helper.Downloader
import com.paulcoding.hviewer.helper.GithubParser
import com.paulcoding.hviewer.helper.GlobalData
import com.paulcoding.hviewer.helper.readConfigFile
import com.paulcoding.hviewer.model.SiteConfigs
import com.paulcoding.hviewer.network.GithubRemoteDatasource
import com.paulcoding.hviewer.preference.Preferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class SiteConfigsRepository(
    private val context: Context,
    private val ioScope: CoroutineScope,
    private val preferences: Preferences,
    private val downloader: Downloader,
    private val githubRemoteDatasource: GithubRemoteDatasource
) {
    val remoteUrl get() = preferences.remoteUrl
    val branch get() = preferences.branch

    private var _siteConfigs = MutableStateFlow<SiteConfigs?>(null)
    val siteConfigs = _siteConfigs.asStateFlow()

    init {
        refreshConfigs()

        _siteConfigs.onEach {
            it?.toHostsMap()?.let { config ->
                GlobalData.siteConfigMap = config
            }
        }.launchIn(ioScope)
    }

    fun refreshConfigs() {
        ioScope.launch {
            getLocalConfigs()
                .onSuccess { _siteConfigs.value = it }
                .onFailure { exception ->
                    exception.printStackTrace()
                }
        }
    }

    suspend fun getLocalConfigs() = runCatching { context.readConfigFile<SiteConfigs>() }

    suspend fun getRemoteConfigs(repoUrl: String, branch: String) =
        runCatching { githubRemoteDatasource.getRemoteSiteConfigs(repoUrl, branch) }

    suspend fun saveRemoteScripts(remoteConfigs: SiteConfigs) = runCatching {
        val remoteUrl = preferences.remoteUrl
        val branch = preferences.branch

        val (owner, repo) = GithubParser.parseRepo(remoteUrl)

        val tarUrl = "https://api.github.com/repos/$owner/$repo/tarball/$branch"

        downloader.saveGz(tarUrl, context.filesDir)

        _siteConfigs.value = remoteConfigs
        preferences.remoteUrl = remoteUrl
    }
}