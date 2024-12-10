package com.paulcoding.hviewer.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.paulcoding.hviewer.model.Tag

class Converters {
    @TypeConverter
    fun fromListTagToString(list: List<Tag>?): String? {
        return list?.let { Gson().toJson(it) }
    }

    @TypeConverter
    fun fromStringToListTag(data: String?): List<Tag>? {
        return data?.let {
            val listType = object : TypeToken<List<String>>() {}.type
            Gson().fromJson(it, listType)
        }
    }
}
