package com.techdroidcentre.albums

import androidx.media3.common.MediaItem

data class AlbumsUiState(
    val albums: List<Album> = emptyList(),
    val error: String = "",
    val loading: Boolean = false
    )

data class Album(
    val id: Long,
    val uri: String,
    val name: String,
    val artist: String,
    val artworkUri: String,
    val noOfSongs: Int,
    val year: Int,
)

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