package com.paulcoding.hviewer.repository

import com.paulcoding.hviewer.helper.Downloader
import com.paulcoding.hviewer.network.GithubRemoteDatasource
import java.io.File

class UpdateAppRepositoryImpl(
    private val githubRemoteDatasource: GithubRemoteDatasource, private val downloader: Downloader
) : UpdateAppRepository {
    override suspend fun getLatestAppRelease() = runCatching {
        githubRemoteDatasource.getLatestAppRelease()
    }

    override suspend fun downloadApk(downloadUrl: String, destination: File) = runCatching {
        downloader.download(downloadUrl, destination)
    }
}