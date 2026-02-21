package com.paulcoding.hviewer.repository

import com.paulcoding.hviewer.database.PostItemDao
import com.paulcoding.hviewer.model.PostItem

class HistoryRepository(private val postItemDao: PostItemDao) {
    val viewedPosts = postItemDao.getViewedPosts()

    suspend fun setViewed(postItem: PostItem, viewed: Boolean) {
        postItemDao.setViewed(postItem.url, viewed = viewed)
    }

    suspend fun clear() {
        postItemDao.clearHistory()
    }
}