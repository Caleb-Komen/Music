package com.techdroidcentre.data.mapper

import com.techdroidcentre.database.model.PlaylistEntity
import com.techdroidcentre.database.model.PlaylistSongEntity
import com.techdroidcentre.model.Playlist
import com.techdroidcentre.model.Song

fun Playlist.toEntity() = PlaylistEntity(
    id = id,
    name = name
)

fun PlaylistEntity.toModel() = Playlist(
    id = id,
    name = name
)

fun Song.toEntity(playlistId: Long) = PlaylistSongEntity(
    id = id,
    uri = uri,
    title = title,
    album = album,
    artist = artist,
    trackNumber = trackNumber,
    artworkUri = artworkUri,
    artworkData = artworkData,
    playlistId = playlistId
)

fun PlaylistSongEntity.toModel() = Song(
    id = id,
    uri = uri,
    title = title,
    album = album,
    artist = artist,
    trackNumber = trackNumber,
    artworkUri = artworkUri,
    artworkData = artworkData
)