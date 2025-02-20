package com.paulcoding.hviewer.model

import androidx.room.Entity
import androidx.room.PrimaryKey

data class PostData(
    val images: List<String>,
    val total: Int,
    val next: String? = null
)


@Entity(tableName = "favorite_posts")
data class PostItem(
    @PrimaryKey
    val url: String = "",
    val name: String = "",
    val thumbnail: String = "",
    val site: String = "",
    val createdAt: Long = 0,
    val tags: List<Tag>? = null,
    val size: Int? = null,
    val views: Int? = null,
    val quantity: Int? = null,
) {
    fun getHost(): String {
        return url.split("/")[2]
    }

    fun getSiteConfig(hostsMap: Map<String, SiteConfig>): SiteConfig? {
        val host = getHost()
        return hostsMap[host]
    }
}

// duplicated?
@Entity(tableName = "history")
data class PostHistory(
    @PrimaryKey
    val url: String = "",
    val name: String = "",
    val thumbnail: String = "",
    val site: String = "",
    val createdAt: Long = 0,
    val tags: List<Tag>? = null,
    val size: Int? = null,
    val views: Int? = null,
    val quantity: Int? = null,
) {
    fun toPostItem(): PostItem {
        return PostItem(
            url = url,
            name = name,
            thumbnail = thumbnail,
            site = site,
            createdAt = createdAt,
            tags = tags,
            size = size,
            views = views
        )
    }
}

data class Tag(
    val name: String = "",
    val url: String = "",
)

data class Posts(
    val posts: List<PostItem> = listOf(),
    val total: Int = 1,
    val next: String? = null
)