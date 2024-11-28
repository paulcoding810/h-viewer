package com.paulcoding.hviewer.model

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage

data class SiteConfig(
    val baseUrl: String = "",
    val scriptFile: String = "",
    val tags: Map<String, String> = mapOf(),
) {
    private val icon
        get() = "https://www.google.com/s2/favicons?sz=64&domain=$baseUrl"

    @Composable
    fun SiteIcon(size: Dp = 20.dp, clip: Dp = 4.dp) {
        AsyncImage(
            icon, baseUrl,
            modifier = Modifier
                .size(size)
                .clip(RoundedCornerShape(clip)),
            contentScale = ContentScale.Crop,
        )
    }
}

data class SiteConfigs(
    val version: Int = 1,
    val sites: Map<String, SiteConfig> = mapOf()
)