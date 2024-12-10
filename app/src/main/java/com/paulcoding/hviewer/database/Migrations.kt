package com.paulcoding.hviewer.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE favorite_posts ADD COLUMN site TEXT NOT NULL DEFAULT \"\"")
    }
}

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE favorite_posts ADD COLUMN createdAt INTEGER NOT NULL DEFAULT 0")
    }
}

val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE favorite_posts ADD COLUMN tags TEXT")
        db.execSQL("ALTER TABLE favorite_posts ADD COLUMN size INTEGER")
        db.execSQL("ALTER TABLE favorite_posts ADD COLUMN views INTEGER")
        db.execSQL("ALTER TABLE favorite_posts ADD COLUMN quantity INTEGER")
    }
}