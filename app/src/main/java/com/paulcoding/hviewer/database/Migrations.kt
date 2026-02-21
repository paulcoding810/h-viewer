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

val MIGRATION_5_6 = object : Migration(5, 6) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // Step 1: Create a new table post_items
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS post_items (
                url TEXT NOT NULL, 
                name TEXT NOT NULL, 
                thumbnail TEXT NOT NULL, 
                createdAt INTEGER NOT NULL, 
                tags TEXT, 
                size INTEGER, 
                views INTEGER, 
                quantity INTEGER, 
                favorite INTEGER NOT NULL, 
                favoriteAt INTEGER NOT NULL, 
                viewed INTEGER NOT NULL, 
                viewedAt INTEGER NOT NULL, 
                PRIMARY KEY(`url`)
            )
        """.trimIndent()
        )

        // Step 2: Copy data from favorite_posts to post_items
        db.execSQL(
            """
            INSERT INTO post_items (url, name, thumbnail, createdAt, tags, size, views, quantity, favorite, favoriteAt, viewed, viewedAt)
            SELECT url, name, thumbnail, createdAt, tags, size, views, quantity, 1, createdAt, 1, createdAt FROM favorite_posts
        """.trimIndent()
        )

        // Step 3: Drop tables
        db.execSQL("DROP TABLE favorite_posts")
        db.execSQL("DROP TABLE history")
    }
}


val MIGRATION_6_7 = object : Migration(6, 7) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // Step 1: Create a new tables favorite, history
        db.execSQL(
            """
                CREATE TABLE IF NOT EXISTS favorite (
                    `url`        TEXT NOT NULL,
                    `name`       TEXT NOT NULL,
                    `thumbnail`  TEXT NOT NULL,
                    `tags`       TEXT,
                    `favoriteAt` INTEGER NOT NULL,
                    PRIMARY KEY (`url`)
                );
        """.trimIndent()
        )
        db.execSQL(
            """
              CREATE TABLE IF NOT EXISTS history (
                    `url`        TEXT NOT NULL,
                    `name`       TEXT NOT NULL,
                    `thumbnail`  TEXT NOT NULL,
                    `tags`       TEXT,
                    `viewedAt` INTEGER NOT NULL,
                    PRIMARY KEY (`url`)
              );
        """.trimIndent()
        )

        // Step 2: Copy data from post_items to favorite, history
        db.execSQL(
            """
            INSERT INTO favorite (url, name, thumbnail, tags, favoriteAt)
            SELECT url, name, thumbnail, tags, favoriteAt FROM post_items WHERE favorite = 1
        """.trimIndent()
        )
        db.execSQL(
            """
            INSERT INTO history (url, name, thumbnail, tags, viewedAt)
            SELECT url, name, thumbnail, tags, viewedAt
            FROM post_items WHERE viewed = 1
        """.trimIndent()
        )

        // Step 3: Drop tables
        db.execSQL("DROP TABLE post_items")
    }
}

val migrations = arrayOf(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6, MIGRATION_6_7)