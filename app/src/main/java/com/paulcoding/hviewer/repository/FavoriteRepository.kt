package com.paulcoding.hviewer.repository

import com.paulcoding.hviewer.database.PostItemDao
import com.paulcoding.hviewer.model.PostItem

class FavoriteRepository(private val postItemDao: PostItemDao) {
    val favoritePosts = postItemDao.getFavoritePosts()
    val favoritePostUrls = postItemDao.getFavoriteUrls()

    val isFavorite =
        { url: String -> postItemDao.isFavorite(url) }

    suspend fun addFavorite(postItem: PostItem, reAdded: Boolean = false) {
        postItemDao.setFavorite(
            postItem.url,
            favorite = true,
            favoriteAt = if (!reAdded) System.currentTimeMillis() else postItem.favoriteAt,
        )
    }

    suspend fun deleteFavorite(postItem: PostItem) {
        postItemDao.setFavorite(
            postItem.url, false
        )
    }

    suspend fun toggleFavorite(postItem: PostItem) {
        println("🚀 ~ postItem: ${postItem.favorite}")
        if (postItem.favorite) {
            deleteFavorite(postItem)
        } else {
            addFavorite(postItem)
        }
    }
}