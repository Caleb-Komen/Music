package com.techdroidcentre.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.techdroidcentre.database.model.PlaylistSongCrossRef
import com.techdroidcentre.database.model.PlaylistSongEntity
import com.techdroidcentre.database.model.PlaylistWithSongs
import com.techdroidcentre.database.model.SongWithPlaylists
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistSongsDao {
    @Transaction
    @Query("SELECT * FROM playlists  WHERE id = :playlistId")
    fun getPlaylistSongs(playlistId: Long): Flow<PlaylistWithSongs>

    @Transaction
    @Query("SELECT * FROM playlist_songs  WHERE id = :songId")
    fun getPlaylistsWithSong(songId: Long): Flow<SongWithPlaylists>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(vararg join: PlaylistSongCrossRef)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPlaylistSongs(vararg entity: PlaylistSongEntity)

    @Query("DELETE FROM playlists_songs WHERE playlist_id = :playlistId AND song_id = :songId")
    suspend fun deletePlaylistSong(playlistId: Long, songId: Long)

    @Query("DELETE FROM playlist_songs WHERE id = :songId")
    suspend fun deletePlaylistSong(songId: Long)
}