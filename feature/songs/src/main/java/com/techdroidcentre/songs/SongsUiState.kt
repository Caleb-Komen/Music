package com.techdroidcentre.songs

import androidx.media3.common.MediaItem
import com.techdroidcentre.model.Song

data class SongsUiState(
    val songs: List<Song> = emptyList(),
    val error: String = "",
    val loading: Boolean = false,
)

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