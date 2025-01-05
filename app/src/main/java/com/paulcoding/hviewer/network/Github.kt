package com.paulcoding.hviewer.network

import com.google.gson.Gson
import com.paulcoding.hviewer.MainApp.Companion.appContext
import com.paulcoding.hviewer.helper.alsoLog
import com.paulcoding.hviewer.helper.configFile
import com.paulcoding.hviewer.helper.extractTarGzFromResponseBody
import com.paulcoding.hviewer.helper.log
import com.paulcoding.hviewer.helper.readConfigFile
import com.paulcoding.hviewer.model.SiteConfigs
import com.paulcoding.hviewer.preference.Preferences
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.readRawBytes
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext

object Github {
    private var _stateFlow = MutableStateFlow(GithubState())
    val stateFlow = _stateFlow.asStateFlow()

    data class GithubState(
        val remoteUrl: String = Preferences.getRemote(),
        val isLoading: Boolean = false,
        val error: Throwable? = null,
        val siteConfigs: SiteConfigs? = appContext.readConfigFile<SiteConfigs>().getOrNull()
    )

    private fun setError(throwable: Throwable) {
        throwable.printStackTrace()
        _stateFlow.update { it.copy(error = throwable) }
    }

    suspend fun checkVersionOrUpdate() {
        withContext(Dispatchers.IO) {
            try {
                if (_stateFlow.value.remoteUrl != Preferences.getRemote()) {
                    Preferences.setRemote(_stateFlow.value.remoteUrl)
                    _stateFlow.update { it.copy(isLoading = true) }
                    downloadAndGetConfig()
                    return@withContext
                }
                if (!appContext.configFile.exists()) {
                    log("Config file not exist, downloading from remote...", "check update")
                    _stateFlow.update { it.copy(isLoading = true) }
                    downloadAndGetConfig()
                    return@withContext
                }

                val currentConfigs =
                    appContext.readConfigFile<SiteConfigs>().getOrNull()

                if (currentConfigs == null) {
                    log("Can not read config file, downloading from remote...", "check update")
                    _stateFlow.update { it.copy(isLoading = true) }
                    downloadAndGetConfig()
                } else {
                    val siteConfigs = getSiteConfigs()
                    val version = siteConfigs.version
                    if (version > currentConfigs.version) {
                        log(
                            "Found newer version $version > ${currentConfigs.version}, downloading from remote...",
                            "check update"
                        )
                        downloadAndGetConfig()
                    } else {
                        log("Already latest version", "check update")
                        _stateFlow.update { it.copy(siteConfigs = currentConfigs) }
                    }
                }
            } catch (e: Exception) {
                setError(e)
            } finally {
                _stateFlow.update { it.copy(isLoading = false) }
            }
        }
    }

    fun refreshLocalConfigs() {
        appContext.readConfigFile<SiteConfigs>()
            .onSuccess { configs ->
                _stateFlow.update { it.copy(siteConfigs = configs) }
            }
            .onFailure { setError(it) }
    }

    private suspend fun getSiteConfigs(): SiteConfigs {
        val repoUrl = Preferences.getRemote()

        val (owner, repo) = parseRepo(repoUrl)

        val configUrl =
            "https://raw.githubusercontent.com/$owner/$repo/refs/heads/main/config.json?v=1"

        ktorClient.use { client ->
            val response = client.get(configUrl)
            if (response.status != HttpStatusCode.OK)
                throw Exception("Invalid repo")
            val resultText: String = response.body()
            return Gson().fromJson(resultText, SiteConfigs::class.java)
        }
    }

    private suspend fun downloadAndGetConfig() {
        downloadRepo()
        appContext.readConfigFile<SiteConfigs>().getOrNull().also { configs ->
            _stateFlow.update { it.copy(siteConfigs = configs) }
        }
    }

    private suspend fun downloadRepo() {
        val repoUrl = Preferences.getRemote()

        val (owner, repo) = parseRepo(repoUrl)

        val tarUrl =
            "https://api.github.com/repos/$owner/$repo/tarball".alsoLog("tarUrl")

        ktorClient.use { client ->
            val inputStream = client.get(tarUrl).readRawBytes().inputStream()
            extractTarGzFromResponseBody(inputStream, appContext.filesDir)
        }
    }

    private fun parseRepo(url: String): Pair<String, String> {
        val regex = """^https://github\.com/([^/]+)/([^/]+)/?""".toRegex()

        val matchResult = regex.find(url.trim())
            ?: throw IllegalArgumentException("Invalid repo url. $url")

        val owner = matchResult.groupValues[1]
        val repo = matchResult.groupValues[2]
        return owner to repo
    }

    fun updateRemoteUrl(url: String) {
        kotlin.runCatching {
            val (owner, repo) = parseRepo(url)
            val remoteUrl = "https://github.com/$owner/$repo/"
            _stateFlow.update { it.copy(remoteUrl = remoteUrl) }
        }.onFailure {
            setError(it)
        }
    }
}

