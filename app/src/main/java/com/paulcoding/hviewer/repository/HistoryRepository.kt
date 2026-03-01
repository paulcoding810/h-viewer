package com.paulcoding.hviewer.repository

import com.paulcoding.hviewer.database.HistoryDao
import com.paulcoding.hviewer.model.HistoryEntity
import com.paulcoding.hviewer.model.PostItem
import com.paulcoding.hviewer.model.toHistoryEntity

class HistoryRepository(private val historyDao: HistoryDao) {
    val viewedPosts = historyDao.getAll()

    suspend fun insert(postItem: PostItem) {
        historyDao.insert(postItem.toHistoryEntity())
    }

    suspend fun delete(item: HistoryEntity) {
        historyDao.delete(item)
    }

    suspend fun deleteAll() {
        historyDao.deleteAll()
    }
}