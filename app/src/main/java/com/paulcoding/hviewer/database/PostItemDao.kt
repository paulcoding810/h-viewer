package com.paulcoding.hviewer.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.paulcoding.hviewer.model.PostItem
import kotlinx.coroutines.flow.Flow

@Dao
interface PostItemDao {
    @Query("SELECT * FROM post_items WHERE favorite = 1 ORDER BY favoriteAt DESC")
    fun getFavoritePosts(): Flow<List<PostItem>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addPost(postItem: PostItem)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addPosts(postItems: List<PostItem>)

    @Query("SELECT * FROM post_items where viewed = 1 ORDER BY viewedAt DESC LIMIT 20")
    fun getViewedPosts(): Flow<List<PostItem>>

    @Update
    suspend fun updatePost(postItem: PostItem)
}