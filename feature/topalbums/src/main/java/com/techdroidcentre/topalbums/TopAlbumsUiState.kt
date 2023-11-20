package com.techdroidcentre.topalbums

import com.techdroidcentre.model.Album

data class TopAlbumsUiState(
    val topAlbums: List<Album> = emptyList(),
    val error: String = "",
    val loading: Boolean = false
)
