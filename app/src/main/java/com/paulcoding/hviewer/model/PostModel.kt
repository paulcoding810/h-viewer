package com.paulcoding.hviewer.model

data class PostData(
    val images: List<String>,
    val total: Int,
)


data class PostItem(
    val name: String = "",
    val url: String = "",
    val thumbnail: String = "",
)

data class Posts(
    val posts: List<PostItem> = listOf(),
    val total: Int = 1,
)