package com.paulcoding.hviewer.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.paulcoding.hviewer.model.PostItem

@Database(entities = [PostItem::class], version = 3, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoritePostDao(): FavoritePostDao
}