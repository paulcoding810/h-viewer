package com.paulcoding.hviewer.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.paulcoding.hviewer.model.PostItem
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoritePostDao {
    @Query("SELECT * FROM favorite_posts")
    fun getAll(): Flow<List<PostItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(post: PostItem)

    @Delete
    suspend fun delete(post: PostItem)
}