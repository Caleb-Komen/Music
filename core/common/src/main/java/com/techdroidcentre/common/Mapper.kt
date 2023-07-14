package com.techdroidcentre.common

import androidx.media3.common.MediaItem
import com.techdroidcentre.model.Album
import com.techdroidcentre.model.Artist
import com.techdroidcentre.model.Song

fun MediaItem.toSong(): Song {
    return Song(
        id = mediaId.toLong(),
        uri = localConfiguration?.uri.toString(),
        title = mediaMetadata.title.toString(),
        album = mediaMetadata.albumTitle.toString(),
        artist = mediaMetadata.artist.toString(),
        trackNumber = mediaMetadata.trackNumber!!,
        artworkUri = mediaMetadata.artworkUri.toString(),
        artworkData = mediaMetadata.artworkData
    )
}

fun MediaItem.toAlbum(): Album {
    return Album(
        id = mediaId.toLong(),
        uri = localConfiguration?.uri.toString(),
        name = mediaMetadata.title.toString(),
        artist = mediaMetadata.artist.toString(),
        artworkUri = mediaMetadata.artworkUri.toString(),
        noOfSongs = mediaMetadata.totalTrackCount!!,
        year = mediaMetadata.recordingYear!!
    )
}

fun MediaItem.toArtist(): Artist {
    return Artist(
        id = mediaId,
        uri = localConfiguration?.uri.toString(),
        name = mediaMetadata.title.toString()
    )
}