package com.techdroidcentre.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.techdroidcentre.database.dao.PlaylistSongsDao
import com.techdroidcentre.database.dao.PlaylistsDao
import com.techdroidcentre.database.dao.RecentlyPlayedDao
import com.techdroidcentre.database.dao.TopAlbumsDao
import com.techdroidcentre.database.model.PlaylistEntity
import com.techdroidcentre.database.model.PlaylistSongCrossRef
import com.techdroidcentre.database.model.PlaylistSongEntity
import com.techdroidcentre.database.model.RecentlyPlayedEntity
import com.techdroidcentre.database.model.TopAlbumEntity
import com.techdroidcentre.database.util.ByteArrayConverter

@TypeConverters(ByteArrayConverter::class)
@Database(
    entities = [
        PlaylistEntity::class,
        PlaylistSongEntity::class,
        PlaylistSongCrossRef::class,
        TopAlbumEntity::class,
        RecentlyPlayedEntity::class
    ],
    version = 1
)
abstract class MusicDatabase: RoomDatabase() {
    abstract val playlistsDao: PlaylistsDao
    abstract val playlistSongsDao: PlaylistSongsDao
    abstract val topAlbumsDao: TopAlbumsDao
    abstract val recentlyPlayedDao: RecentlyPlayedDao
}