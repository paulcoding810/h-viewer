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
    @Query("SELECT * FROM post_items ORDER BY viewedAt DESC")
    fun getPosts(): Flow<List<PostItem>>

    @Query("SELECT * FROM post_items WHERE favorite = 1 ORDER BY favoriteAt DESC")
    fun getFavoritePosts(): Flow<List<PostItem>>

    @Query("SELECT url FROM post_items WHERE favorite = 1 ORDER BY favoriteAt DESC")
    fun getFavoriteUrls(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addPost(postItem: PostItem)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addPosts(postItems: List<PostItem>)

    @Query("SELECT * FROM post_items where viewed = 1 ORDER BY viewedAt DESC LIMIT 20")
    fun getViewedPosts(): Flow<List<PostItem>>

    @Update
    suspend fun updatePost(postItem: PostItem)

    @Query("UPDATE post_items SET viewed = :viewed, viewedAt = :viewedAt WHERE url = :url")
    suspend fun setViewed(
        url: String,
        viewed: Boolean = true,
        viewedAt: Long = System.currentTimeMillis()
    )

    @Query("UPDATE post_items SET favorite = :favorite, favoriteAt = :favoriteAt WHERE url = :url")
    suspend fun setFavorite(
        url: String,
        favorite: Boolean,
        favoriteAt: Long = System.currentTimeMillis()
    )

    @Query("SELECT favorite FROM post_items WHERE url = :url")
    fun isFavorite(url: String): Flow<Boolean>

    @Query("UPDATE post_items SET viewed = 0, viewedAt = 0 WHERE viewed = 1")
    suspend fun clearHistory()
}