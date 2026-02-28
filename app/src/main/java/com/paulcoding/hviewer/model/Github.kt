package com.paulcoding.hviewer.model

import com.paulcoding.hviewer.BuildConfig
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable data class Release(
    val url: String,
    val id: Int,
    @SerialName("tag_name")
    val tagName: String,
    val assets: List<Asset>,

    )

@Serializable
data class Asset(
    @SerialName("browser_download_url")
    val browserDownloadUrl: String,
)

@Serializable
data class HRelease(
    val version: String,
    val downloadUrl: String
)

val HRelease.isUpdatable
    get(): Boolean {
        val currentVersion = BuildConfig.VERSION_NAME.split(".").map { it.toIntOrNull() ?: 0 }
        val releaseVersion = version.split(".").map { it.toIntOrNull() ?: 0 }

        // Compare major, minor, and patch versions
        for (i in 0 until maxOf(currentVersion.size, releaseVersion.size)) {
            val current = currentVersion.getOrElse(i) { 0 }
            val release = releaseVersion.getOrElse(i) { 0 }

            if (release > current) return true
            if (release < current) return false
        }

        return false
    }