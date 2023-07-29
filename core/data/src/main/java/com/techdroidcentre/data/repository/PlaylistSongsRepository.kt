package com.techdroidcentre.data.repository

import com.techdroidcentre.model.Song
import kotlinx.coroutines.flow.Flow

interface PlaylistSongsRepository {
    fun getPlaylistSongs(playlistId: Long): Flow<List<Song>>

    suspend fun insertPlaylistSongs(playlistId: Long, vararg song: Song)

    suspend fun deletePlaylistSong(songId: Long)
}