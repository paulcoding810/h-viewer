package com.paulcoding.hviewer.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.paulcoding.hviewer.model.PostHistory
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {
    @Query("SELECT * FROM history ORDER BY createdAt DESC")
    fun getAll(): Flow<List<PostHistory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(history: PostHistory)

    @Delete
    suspend fun delete(history: PostHistory)

    @Query("DELETE FROM history WHERE url = (SELECT url FROM history ORDER BY createdAt ASC LIMIT 1)")
    suspend fun deleteOldest()
}