package com.techdroidcentre.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "top_albums")
data class TopAlbumEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val albumId: Long,
    @ColumnInfo(name = "total_play_count")
    val totalPlayCount: Int
)
