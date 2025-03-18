package com.paulcoding.hviewer.model

data class Release(
    val url: String,
    val id: Int,
    val tag_name: String,
    val assets: List<Asset>,

    )

data class Asset(
    val browser_download_url: String,
)