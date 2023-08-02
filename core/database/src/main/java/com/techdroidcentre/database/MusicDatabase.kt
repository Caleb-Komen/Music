package com.techdroidcentre.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.techdroidcentre.database.dao.PlaylistSongsDao
import com.techdroidcentre.database.dao.PlaylistsDao
import com.techdroidcentre.database.model.PlaylistEntity
import com.techdroidcentre.database.model.PlaylistSongEntity
import com.techdroidcentre.database.util.ByteArrayConverter

@TypeConverters(ByteArrayConverter::class)
@Database(entities = [PlaylistEntity::class, PlaylistSongEntity::class], version = 1)
abstract class MusicDatabase: RoomDatabase() {
    abstract val playlistsDao: PlaylistsDao
    abstract val playlistSongsDao: PlaylistSongsDao
}