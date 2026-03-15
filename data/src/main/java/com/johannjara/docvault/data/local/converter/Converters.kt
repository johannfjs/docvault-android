package com.johannjara.docvault.data.local.converter

import androidx.room.TypeConverter
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object Converters {
    @TypeConverter
    fun fromList(value: List<Long>): String {
        return Json.encodeToString(value)
    }

    @TypeConverter
    fun toList(value: String): List<Long> {
        return Json.decodeFromString(value)
    }
}
