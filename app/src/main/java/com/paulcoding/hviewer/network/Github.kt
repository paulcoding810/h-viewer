package com.paulcoding.hviewer.network

import android.content.Context
import com.google.gson.Gson
import com.paulcoding.hviewer.BuildConfig
import com.paulcoding.hviewer.MainApp.Companion.appContext
import com.paulcoding.hviewer.R
import com.paulcoding.hviewer.helper.extractTarGzFromResponseBody
import com.paulcoding.hviewer.helper.log
import com.paulcoding.hviewer.helper.readConfigFile
import com.paulcoding.hviewer.model.HRelease
import com.paulcoding.hviewer.model.Release
import com.paulcoding.hviewer.model.SiteConfigs
import com.paulcoding.hviewer.preference.Preferences
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.readRawBytes
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

object Github {
    @Throws(Exception::class)
    suspend fun checkVersionOrUpdate(remoteUrl: String = Preferences.getRemote()): SiteConfigsState {
        if (remoteUrl.isEmpty()) {
            throw (Exception("Remote url is empty"))
        }
        return withContext(Dispatchers.IO) {
            if (remoteUrl != Preferences.getRemote()) {
                downloadAndGetConfig(remoteUrl)
                return@withContext SiteConfigsState.NewConfigsInstall(remoteUrl)
            }

            val currentConfigs =
                appContext.readConfigFile<SiteConfigs>().getOrNull()

            if (currentConfigs == null) {
                log("Can not read config file, downloading from remote...", "check update")
                downloadAndGetConfig()
                return@withContext SiteConfigsState.NewConfigsInstall(remoteUrl)
            } else {
                val version = getRemoteVersion()
                if (version > currentConfigs.version) {
                    log(
                        "Found newer version $version > ${currentConfigs.version}, downloading from remote...",
                        "check update"
                    )
                    downloadAndGetConfig()
                    return@withContext SiteConfigsState.Updated(version)
                } else {
                    log("Already latest version", "check update")
                    return@withContext SiteConfigsState.UpToDate(currentConfigs.version)
                }
            }
        }
    }

    private suspend fun getRemoteVersion(): Int {
        val repoUrl = Preferences.getRemote()
        val branch = Preferences.branch

        val (owner, repo) = parseRepo(repoUrl)

        val configUrl =
            "https://raw.githubusercontent.com/$owner/$repo/refs/heads/$branch/config.json?v=1"

        ktorClient.use { client ->
            val response = client.get(configUrl)
            if (response.status != HttpStatusCode.OK)
                throw Exception("Invalid repo")
            val resultText: String = response.body()
            val remoteConfigs = Gson().fromJson(resultText, SiteConfigs::class.java)
            return remoteConfigs.version
        }
    }

    private suspend fun downloadAndGetConfig(remoteUrl: String = Preferences.getRemote()) {
        val branch = Preferences.branch

        val (owner, repo) = parseRepo(remoteUrl)

        val tarUrl =
            "https://api.github.com/repos/$owner/$repo/tarball/$branch"

        ktorClient.use { client ->
            val response = client.get(tarUrl)
            if (response.status == HttpStatusCode.OK) {
                Preferences.setRemote(remoteUrl)
                val inputStream = client.get(tarUrl).readRawBytes().inputStream()
                extractTarGzFromResponseBody(inputStream, appContext.filesDir)
            } else {
                throw Exception("Invalid repo")
            }
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

    fun parseRemoteUrl(url: String): String? {
        try {
            val (owner, repo) = parseRepo(url)
            val remoteUrl = "https://github.com/$owner/$repo/"
            return remoteUrl
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    suspend fun checkForUpdate(
        currentVersion: String,
        onUpdateAvailable: ((String, String) -> Unit)? = null
    ): HRelease? {
        val (owner, repo) = parseRepo(BuildConfig.REPO_URL)
        val url = "https://api.github.com/repos/${owner}/${repo}/releases/latest"
        ktorClient.use { client ->
            val jsonObject: Release = client.get(url).body()
            val latestVersion = jsonObject.tag_name.substring(1)
            val downloadUrl = jsonObject.assets[0].browser_download_url
            if (latestVersion != currentVersion) {
                onUpdateAvailable?.invoke(latestVersion, downloadUrl)
                return HRelease(latestVersion, downloadUrl)
            }
            return null
        }
    }

    suspend fun downloadApk(
        context: Context,
        downloadUrl: String,
        onDownloadComplete: (File) -> Unit
    ) {
        val file = File(context.cacheDir, "latest.apk")
        ktorClient.use { client ->
            val input = client.get(downloadUrl).readRawBytes().inputStream()
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        onDownloadComplete(file)
    }
}


sealed class SiteConfigsState {
    class NewConfigsInstall(val repoUrl: String) : SiteConfigsState()
    class UpToDate(val currentVersion: Int) : SiteConfigsState()
    class Updated(val newVersion: Int) : SiteConfigsState()

    fun getToastMessage() = when (this) {
        is NewConfigsInstall -> R.string.scripts_installed
        is UpToDate -> R.string.up_to_date
        is Updated -> R.string.scripts_updated
    }
}