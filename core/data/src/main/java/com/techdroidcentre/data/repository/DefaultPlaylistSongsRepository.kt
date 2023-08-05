package com.techdroidcentre.data.repository

import com.techdroidcentre.data.mapper.toEntity
import com.techdroidcentre.data.mapper.toModel
import com.techdroidcentre.database.dao.PlaylistSongsDao
import com.techdroidcentre.database.model.PlaylistSongCrossRef
import com.techdroidcentre.database.model.PlaylistSongEntity
import com.techdroidcentre.model.Song
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class DefaultPlaylistSongsRepository(
    private val playlistSongsDao: PlaylistSongsDao
): PlaylistSongsRepository {
    override fun getPlaylistSongs(playlistId: Long): Flow<List<Song>> {
        return playlistSongsDao.getPlaylistSongs(playlistId).map {
            it.songs.map(PlaylistSongEntity::toModel)
        }
    }

    override suspend fun insertPlaylistSongs(playlistId: Long, vararg song: Song) {
        // Order of invocation matters to satisfy id and foreign key constraints!
        playlistSongsDao.insertPlaylistSongs(*(song.map { it.toEntity() }.toTypedArray()))
        playlistSongsDao.insert(*(song.map { PlaylistSongCrossRef(playlistId, it.id) }.toTypedArray()))
    }

    override suspend fun deletePlaylistSong(playlistId: Long, songId: Long) {
        playlistSongsDao.deletePlaylistSong(playlistId, songId)
        val playlists = playlistSongsDao.getPlaylistsWithSong(songId).first().playlists
        if (playlists.isEmpty()) playlistSongsDao.deletePlaylistSong(songId)
    }
}