package com.techdroidcentre.data.repository

import com.techdroidcentre.model.Playlist
import kotlinx.coroutines.flow.Flow

interface PlaylistsRepository {
    fun getPlaylists(): Flow<List<Playlist>>

    fun getPlaylist(id: Long): Flow<Playlist>

    suspend fun insertPlaylist(playlist: Playlist)

    suspend fun updatePlaylist(playlist: Playlist)

    suspend fun deletePlaylist(id: Long)
}