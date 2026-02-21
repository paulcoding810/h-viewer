package com.paulcoding.hviewer.helper

import com.paulcoding.hviewer.exception.AppException

object GithubParser {
    fun parseRepo(url: String): Pair<String, String> {
        val regex = """^https://github\.com/([^/]+)/([^/]+)/?""".toRegex()

        val matchResult = regex.find(url.trim())
            ?: throw AppException.InvalidRepositoryException(url)

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
}