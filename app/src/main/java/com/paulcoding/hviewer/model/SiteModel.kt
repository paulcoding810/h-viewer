package com.paulcoding.hviewer.model

data class SiteConfig(
    val baseUrl: String = "",
    val scriptFile: String = "",
    val tags: Map<String, String> = mapOf(),
)

data class SiteConfigs(
    val version: Int = 1,
    val sites: Map<String, SiteConfig> = mapOf()
)