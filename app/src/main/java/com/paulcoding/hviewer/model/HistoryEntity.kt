package com.paulcoding.hviewer.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity("history")
@Serializable
data class HistoryEntity(
    @PrimaryKey
    val url: String = "",
    val name: String = "",
    val thumbnail: String = "",
    val tags: List<Tag>? = null,
    val viewedAt: Long = System.currentTimeMillis(),
)

fun PostItem.toHistoryEntity() = HistoryEntity(
    url = url,
    name = name,
    thumbnail = thumbnail,
    tags = tags,
    viewedAt = System.currentTimeMillis()
)

fun HistoryEntity.toPostItem(): PostItem {
    return PostItem(
        url = url,
        name = name,
        thumbnail = thumbnail,
        tags = tags,
        viewed = true,
        viewedAt = viewedAt,
    )
}