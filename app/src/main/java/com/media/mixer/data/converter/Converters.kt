package com.media.mixer.data.converter

import android.net.Uri
import androidx.core.net.toUri
import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromMap(map: Map<Long, Boolean>): String {
        val stringBuilder = StringBuilder()
        for ((key, value) in map) {
            stringBuilder.append("$key=${if (value) "1" else "0"},")
        }
        return stringBuilder.toString()
    }

    @TypeConverter
    fun toMap(data: String): Map<Long, Boolean> {
        val map = mutableMapOf<Long, Boolean>()
        val pairs = data.split(",")
        for (pair in pairs) {
            if (pair.isNotEmpty()) {
                val (key, value) = pair.split("=")
                map[key.toLong()] = value == "1"
            }
        }
        return map
    }

    @TypeConverter
    fun fromUri(uri: Uri): String {
        return uri.toString()
    }

    @TypeConverter
    fun toUri(string: String): Uri {
        return string.toUri()
    }
}
