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

val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `history` (`url` TEXT NOT NULL, `name` TEXT NOT NULL, `thumbnail` TEXT NOT NULL, `site` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, `tags` TEXT, `size` INTEGER, `views` INTEGER, `quantity` INTEGER, PRIMARY KEY(`url`))"
        )
    }
}