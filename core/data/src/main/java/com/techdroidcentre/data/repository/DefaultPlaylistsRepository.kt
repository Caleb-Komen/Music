package com.techdroidcentre.data.repository

import com.techdroidcentre.data.mapper.toEntity
import com.techdroidcentre.data.mapper.toModel
import com.techdroidcentre.database.dao.PlaylistSongsDao
import com.techdroidcentre.database.dao.PlaylistsDao
import com.techdroidcentre.database.model.PlaylistEntity
import com.techdroidcentre.model.Playlist
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DefaultPlaylistsRepository @Inject constructor(
    private val playlistsDao: PlaylistsDao,
    private val playlistSongsDao: PlaylistSongsDao
): PlaylistsRepository {
    override fun getPlaylists(): Flow<List<Playlist>> =
        playlistsDao.getPlaylists().map {
            it.map(PlaylistEntity::toModel)
        }

    override fun getPlaylist(id: Long): Flow<Playlist> =
        playlistsDao.getPlaylist(id).map(PlaylistEntity::toModel)

    override suspend fun insertPlaylist(playlist: Playlist) {
        playlistsDao.insertPlaylist(playlist.toEntity())
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        playlistsDao.updatePlaylist(playlist.toEntity())
    }

    override suspend fun deletePlaylist(id: Long) {
        val songs = playlistSongsDao.getPlaylistSongs(id).first().songs
        songs.forEach {
            playlistSongsDao.deletePlaylistSong(id, it.id)
            val playlists = playlistSongsDao.getPlaylistsWithSong(it.id).first().playlists
            if (playlists.isEmpty()) playlistSongsDao.deletePlaylistSong(it.id)
        }
        playlistsDao.deletePlaylist(id)
    }
}