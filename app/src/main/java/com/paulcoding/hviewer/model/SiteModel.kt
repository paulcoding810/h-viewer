package com.paulcoding.hviewer.model

import android.os.Parcelable
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.paulcoding.hviewer.R

@kotlinx.parcelize.Parcelize
data class SiteConfig(
    val name: String = "",
    val baseUrl: String = "",
    val scriptFile: String = "",
    val tags: Map<String, String> = mapOf(),
) : Parcelable {
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
            placeholder = painterResource(R.drawable.ic_launcher_foreground)
        )
    }
}

data class SiteConfigs(
    val version: Int = 1,
    val sites: List<SiteConfig> = listOf()
) {
    fun toHostsMap(): Map<String, SiteConfig> {
        return sites.associateBy { it.baseUrl.split('/')[2] }
    }
}