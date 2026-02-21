package com.paulcoding.hviewer.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.paulcoding.hviewer.model.FavoriteEntity
import com.paulcoding.hviewer.model.HistoryEntity

@Database(entities = [FavoriteEntity::class, HistoryEntity::class], version = 7, exportSchema = true)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteItemDao(): FavoriteDao
    abstract fun historyDao(): HistoryDao
}