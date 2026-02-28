package com.paulcoding.hviewer.database

import androidx.room.TypeConverter
import com.paulcoding.hviewer.model.Tag
import kotlinx.serialization.json.Json

class Converters {
    @TypeConverter
    fun fromListTagToString(list: List<Tag>?): String? {
        return list?.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun fromStringToListTag(data: String?): List<Tag>? {
        return data?.let {
            Json.decodeFromString<List<Tag>>(data)
        }
    }
}
