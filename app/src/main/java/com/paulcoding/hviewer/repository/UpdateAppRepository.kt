package com.paulcoding.hviewer.repository

import com.paulcoding.hviewer.helper.Downloader
import com.paulcoding.hviewer.network.GithubRemoteDatasource
import java.io.File

class UpdateAppRepository(
    private val githubRemoteDatasource: GithubRemoteDatasource,
    private val downloader: Downloader
) {
    suspend fun getLatestAppRelease() = runCatching {
         githubRemoteDatasource.getLatestAppRelease()
    }

    suspend fun downloadApk(downloadUrl: String, destination: File) = runCatching {
        downloader.download(downloadUrl, destination)
    }
}