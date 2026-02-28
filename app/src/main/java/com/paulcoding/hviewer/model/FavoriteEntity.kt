package com.paulcoding.hviewer.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity("favorite")
@Serializable
data class FavoriteEntity(
    @PrimaryKey
    val url: String = "",
    val name: String = "",
    val thumbnail: String = "",
    val tags: List<Tag>? = null,
    val favoriteAt: Long = System.currentTimeMillis(),
)

fun PostItem.toFavoriteEntity(keepTimestamp: Boolean): FavoriteEntity {
    return FavoriteEntity(
        url = url,
        name = name,
        thumbnail = thumbnail,
        tags = tags,
        favoriteAt = if (keepTimestamp) favoriteAt else System.currentTimeMillis(),
    )
}

fun FavoriteEntity.toPostItem(): PostItem {
    return PostItem(
        url = url,
        name = name,
        thumbnail = thumbnail,
        tags = tags,
        favorite = true,
        favoriteAt = favoriteAt,
    )
}