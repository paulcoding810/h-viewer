package com.paulcoding.hviewer.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.paulcoding.hviewer.model.PostItem

@Database(entities = [PostItem::class], version = 6, exportSchema = true)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun postItemDao(): PostItemDao
}