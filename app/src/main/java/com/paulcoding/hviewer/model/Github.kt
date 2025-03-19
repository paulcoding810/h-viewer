package com.paulcoding.hviewer.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class Release(
    val url: String,
    val id: Int,
    val tag_name: String,
    val assets: List<Asset>,

    )

data class Asset(
    val browser_download_url: String,
)

@Parcelize
data class HRelease(
    val version: String,
    val downloadUrl: String
) : Parcelable