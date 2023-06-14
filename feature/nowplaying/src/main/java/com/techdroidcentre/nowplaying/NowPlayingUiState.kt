package com.techdroidcentre.nowplaying

import androidx.media3.common.MediaItem
import com.techdroidcentre.model.Song

data class NowPlayingUiState(
    val isPlaying: Boolean = false,
    val song: Song = Song(title = "Not Playing"),
    val duration: Long = 0L,
    val position: Long = 0L
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