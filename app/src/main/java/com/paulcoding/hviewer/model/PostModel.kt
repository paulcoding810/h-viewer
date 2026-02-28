package com.paulcoding.hviewer.model

import kotlinx.serialization.Serializable

@Serializable
data class PostData(
    val images: List<String>,
    val total: Int,
    val next: String? = null
)

@Serializable
data class PostItemEntity(
    val url: String = "",
    val name: String = "",
    val thumbnail: String = "",
    val tags: List<Tag>? = null,
    val size: Int? = null,
    val views: Int? = null,
    val quantity: Int? = null,
)

@Serializable
data class PostItem(
    val url: String = "",
    val name: String = "",
    val thumbnail: String = "",
    val tags: List<Tag>? = null,
    val size: Int? = null,
    val views: Int? = null,
    val quantity: Int? = null,
    val favorite: Boolean = false,
    val favoriteAt: Long = 0,
    val viewed: Boolean = false,
    val viewedAt: Long = 0,
    val isDeepLink: Boolean = false,
)

fun PostItemEntity.toPostItem(): PostItem {
    return PostItem(
        url = url,
        name = name,
        thumbnail = thumbnail,
        tags = tags,
        size = size,
        views = views,
        quantity = quantity,
        favorite = false,
        viewed = false
    )
}

@Serializable
data class Tag(
    val name: String = "",
    val url: String = "",
)

@Serializable
data class PostsEntity(
    val posts: List<PostItemEntity> = listOf(),
    val total: Int = 1,
    val next: String? = null
)

@Serializable
data class Posts(
    val posts: List<PostItem> = listOf(),
    val total: Int = 1,
    val next: String? = null
)

fun PostsEntity.toPosts(): Posts {
    return Posts(
        posts = posts.map { it.toPostItem() },
        total = total,
        next = next
    )
}