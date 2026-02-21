package com.paulcoding.hviewer.model

import com.paulcoding.hviewer.helper.host
import kotlinx.serialization.Serializable

@Serializable
data class SiteConfig(
    val name: String = "",
    val baseUrl: String = "",
    val scriptFile: String = "",
    val tags: Map<String, String> = mapOf(),
)

data class SiteConfigs(
    val version: Int = 1,
    val sites: List<SiteConfig> = listOf()
) {
    fun toHostsMap(): Map<String, SiteConfig> {
        return sites.associateBy { it.baseUrl.host}
    }
}