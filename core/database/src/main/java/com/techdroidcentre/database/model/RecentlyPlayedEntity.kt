package com.techdroidcentre.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recently_played")
data class RecentlyPlayedEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val songId: Long,
    @ColumnInfo(name = "time")
    val time: Long
)
