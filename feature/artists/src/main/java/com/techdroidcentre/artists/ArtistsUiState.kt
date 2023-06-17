package com.techdroidcentre.artists

import androidx.media3.common.MediaItem
import com.techdroidcentre.model.Artist

data class ArtistsUiState(
    val artists: List<Artist> = emptyList(),
    val error: String = "",
    val loading: Boolean = false
)

fun MediaItem.toArtist(): Artist {
    return Artist(
        id = mediaId,
        uri = localConfiguration?.uri.toString(),
        name = mediaMetadata.title.toString()
    )
}