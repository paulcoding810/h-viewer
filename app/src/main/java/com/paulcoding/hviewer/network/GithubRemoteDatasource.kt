package com.paulcoding.hviewer.network

import com.google.gson.Gson
import com.paulcoding.hviewer.BuildConfig
import com.paulcoding.hviewer.helper.GithubParser
import com.paulcoding.hviewer.model.HRelease
import com.paulcoding.hviewer.model.Release
import com.paulcoding.hviewer.model.SiteConfigs
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GithubRemoteDatasource(
    private val httpClient: HttpClient,
) {
    suspend fun getRemoteSiteConfigs(repoUrl: String, branch: String): SiteConfigs = withContext(Dispatchers.IO) {
        val (owner, repo) = GithubParser.parseRepo(repoUrl)

        val configUrl =
            "https://raw.githubusercontent.com/$owner/$repo/refs/heads/$branch/config.json?v=1"

        val response = httpClient.get(configUrl) {
            header("Content-Type", "application/json")
        }
        // use string as the content-type here is text/html, not application/json
        val resultText: String = response.body()
        val remoteConfigs = Gson().fromJson(resultText, SiteConfigs::class.java)
        remoteConfigs
    }

    suspend fun getLatestAppRelease(): HRelease {
        val (owner, repo) = GithubParser.parseRepo(BuildConfig.REPO_URL)
        val url = "https://api.github.com/repos/${owner}/${repo}/releases/latest"
        val jsonObject: Release = httpClient.get(url).body()
        val latestVersion = jsonObject.tag_name.substring(1)
        val downloadUrl = jsonObject.assets[0].browser_download_url
        return HRelease(latestVersion, downloadUrl)
    }
}

//
//sealed class SiteConfigsState {
//    class NewConfigsInstall(val repoUrl: String) : SiteConfigsState()
//    class UpToDate(val currentVersion: Int) : SiteConfigsState()
//    class Updated(val newVersion: Int) : SiteConfigsState()
//
//    fun getToastMessage() = when (this) {
//        is NewConfigsInstall -> R.string.scripts_installed
//        is UpToDate -> R.string.up_to_date
//        is Updated -> R.string.scripts_updated
//    }
//}