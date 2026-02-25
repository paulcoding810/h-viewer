package com.paulcoding.hviewer.repository

import com.paulcoding.hviewer.database.FavoriteDao
import com.paulcoding.hviewer.model.PostItem
import com.paulcoding.hviewer.model.toFavoriteEntity

class FavoriteRepository(private val favoriteDao: FavoriteDao) {
    val favoritePosts = favoriteDao.getFavoritePosts()
    val favoritePostUrls = favoriteDao.getFavoriteUrls()

    suspend fun addFavorite(postItem: PostItem, keepTimestamp: Boolean = false) {
        favoriteDao.insert(postItem.toFavoriteEntity(keepTimestamp))
    }

    suspend fun deleteFavorite(postItem: PostItem) {
        favoriteDao.delete(postItem.url)
    }

    suspend fun toggleFavorite(postItem: PostItem) {
        if (postItem.favorite) {
            deleteFavorite(postItem)
        } else {
            addFavorite(postItem)
        }
    }

   fun isFavorite(postItem: PostItem) = favoriteDao.isFavorite(postItem.url)
}