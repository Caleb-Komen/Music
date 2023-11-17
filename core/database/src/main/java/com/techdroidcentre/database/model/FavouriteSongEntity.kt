package com.techdroidcentre.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favourite_songs")
data class FavouriteSongEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val songId: Long
)
