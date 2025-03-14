package com.paulcoding.hviewer.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import com.paulcoding.hviewer.model.PostItem
import com.paulcoding.hviewer.model.SiteConfig

val LocalHostsMap = staticCompositionLocalOf<Map<String, SiteConfig>> { mapOf() }

@Composable
fun rememberSiteConfig(postItem: PostItem): SiteConfig {
    val hostsMap = LocalHostsMap.current
    return remember { hostsMap[postItem.getHost()] ?: SiteConfig() }
}

@Composable
fun rememberSiteConfig(url: String): SiteConfig {
    val hostsMap = LocalHostsMap.current
    val host = url.split("/").getOrNull(2)
    return remember { hostsMap[host] ?: SiteConfig() }
}
