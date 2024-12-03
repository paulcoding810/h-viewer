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
)

data class Posts(
    val posts: List<PostItem> = listOf(),
    val total: Int = 1,
    val next: String? = null
)