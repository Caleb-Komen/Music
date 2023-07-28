package com.techdroidcentre.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.techdroidcentre.database.dao.PlaylistsDao
import com.techdroidcentre.database.model.PlaylistEntity

@Database(entities = [PlaylistEntity::class], version = 1)
abstract class MusicDatabase: RoomDatabase() {
    abstract val playlistsDao: PlaylistsDao
}