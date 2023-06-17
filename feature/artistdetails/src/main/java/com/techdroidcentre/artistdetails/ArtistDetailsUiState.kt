package com.techdroidcentre.artistdetails

import androidx.media3.common.MediaItem
import com.techdroidcentre.model.Album
import com.techdroidcentre.model.Artist

data class ArtistDetailsUiState(
    val artist: String = "",
    val albums: List<Album> = emptyList(),
    val error: String = "",
    val loading: Boolean = false
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

fun MediaItem.toArtist(): Artist {
    return Artist(
        id = mediaId,
        uri = localConfiguration?.uri.toString(),
        name = mediaMetadata.title.toString()
    )
}
