package com.techdroidcentre.database.util

import androidx.room.TypeConverter

class ByteArrayConverter {
    @TypeConverter
    fun toString(bytes: ByteArray?): String {
        if (bytes == null) return ""
        return String(bytes)
    }

    @TypeConverter
    fun fromString(bytes: String): ByteArray? {
        if (bytes.isEmpty()) return null
        return bytes.toByteArray()
    }
}