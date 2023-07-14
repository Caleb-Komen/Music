package com.techdroidcentre.albumdetails

import com.techdroidcentre.model.Album
import com.techdroidcentre.model.Song

data class AlbumDetailsUiState(
    val album: Album = Album(-1L, "", "", "", "", 0, 0),
    val songs: List<Song> = emptyList(),
    val error: String = "",
    val loading: Boolean = false
)
