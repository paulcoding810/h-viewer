package com.paulcoding.hviewer.database

import androidx.room.Room
import com.paulcoding.hviewer.MainApp.Companion.appContext

object DatabaseProvider {
    private var db: AppDatabase? = null

    fun getInstance(): AppDatabase {
        if (db == null) {
            db = Room.databaseBuilder(
                appContext,
                AppDatabase::class.java, "hviewer_db"
            )
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                .build()
        }
        return db!!
    }
}