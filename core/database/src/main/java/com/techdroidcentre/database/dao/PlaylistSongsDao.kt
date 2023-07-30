package com.techdroidcentre.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.techdroidcentre.database.model.PlaylistSongEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistSongsDao {
    @Query("SELECT * FROM playlist_songs  WHERE playlist_id = :playlistId")
    fun getPlaylistSongs(playlistId: Long): Flow<List<PlaylistSongEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPlaylistSongs(vararg entity: PlaylistSongEntity)

    @Query("DELETE FROM playlist_songs WHERE id = :songId")
    suspend fun deletePlaylistSong(songId: Long)
}