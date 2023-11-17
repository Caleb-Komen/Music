package com.techdroidcentre.data.mapper

import com.techdroidcentre.data.model.AlbumEntity
import com.techdroidcentre.data.model.SongEntity
import com.techdroidcentre.database.model.FavouriteSongEntity
import com.techdroidcentre.database.model.PlaylistEntity
import com.techdroidcentre.database.model.PlaylistSongEntity
import com.techdroidcentre.database.model.RecentlyPlayedEntity
import com.techdroidcentre.database.model.TopAlbumEntity
import com.techdroidcentre.model.Album
import com.techdroidcentre.model.FavouriteSong
import com.techdroidcentre.model.Playlist
import com.techdroidcentre.model.RecentlyPlayed
import com.techdroidcentre.model.Song
import com.techdroidcentre.model.TopAlbum

fun Playlist.toEntity() = PlaylistEntity(
    id = id,
    name = name
)

fun PlaylistEntity.toModel() = Playlist(
    id = id,
    name = name
)

fun Song.toEntity() = PlaylistSongEntity(
    id = id,
    uri = uri,
    title = title,
    album = album,
    artist = artist,
    trackNumber = trackNumber,
    artworkUri = artworkUri,
    artworkData = artworkData
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

fun TopAlbum.toEntity() = TopAlbumEntity(albumId = albumId, totalPlayCount = totalPlayCount)

fun TopAlbumEntity.toModel() = TopAlbum(albumId = albumId, totalPlayCount = totalPlayCount)

fun AlbumEntity.toModel() = Album(
    id = id,
    uri = uri,
    name = name,
    artist = artist,
    artworkUri = artworkUri,
    noOfSongs = noOfSongs,
    year = year,
)

fun Album.toEntity() = AlbumEntity(
    id = id,
    uri = uri,
    name = name,
    artist = artist,
    artworkUri = artworkUri,
    noOfSongs = noOfSongs,
    year = year,
)

fun RecentlyPlayed.toEntity() = RecentlyPlayedEntity(songId = songId, time = time)

fun RecentlyPlayedEntity.toModel() = RecentlyPlayed(songId = songId, time = time)

fun FavouriteSong.toEntity() = FavouriteSongEntity(songId = songId)

fun FavouriteSongEntity.toModel() = FavouriteSong(songId = songId)
