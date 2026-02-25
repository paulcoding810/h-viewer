package com.paulcoding.hviewer.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.paulcoding.hviewer.model.FavoriteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorite ORDER BY favoriteAt DESC")
    fun getFavoritePosts(): Flow<List<FavoriteEntity>>

    @Query("SELECT url FROM favorite ORDER BY favoriteAt DESC")
    fun getFavoriteUrls(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(favoriteEntity: FavoriteEntity)

    @Query("DELETE FROM favorite WHERE url = :url")
    suspend fun delete(url: String)

    @Query("SELECT EXISTS (SELECT 1 FROM favorite WHERE url = :url)")
    fun isFavorite(url: String): Flow<Boolean>
}