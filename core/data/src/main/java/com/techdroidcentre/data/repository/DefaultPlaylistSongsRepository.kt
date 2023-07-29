package com.techdroidcentre.data.repository

import com.techdroidcentre.data.mapper.toEntity
import com.techdroidcentre.data.mapper.toModel
import com.techdroidcentre.database.dao.PlaylistSongsDao
import com.techdroidcentre.database.model.PlaylistSongEntity
import com.techdroidcentre.model.Song
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DefaultPlaylistSongsRepository(
    private val playlistSongsDao: PlaylistSongsDao
): PlaylistSongsRepository {
    override fun getPlaylistSongs(playlistId: Long): Flow<List<Song>> {
        return playlistSongsDao.getPlaylistSongs(playlistId).map {
            it.map(PlaylistSongEntity::toModel)
        }
    }

    override suspend fun insertPlaylistSongs(playlistId: Long, vararg song: Song) {
        playlistSongsDao.insertPlaylistSongs(*(song.map { it.toEntity(playlistId) }.toTypedArray()))
    }

    override suspend fun deletePlaylistSong(songId: Long) {
        playlistSongsDao.deletePlaylistSong(songId)
    }
}