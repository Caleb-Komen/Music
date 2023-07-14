package com.techdroidcentre.albums

import com.techdroidcentre.model.Album

data class AlbumsUiState(
    val albums: List<Album> = emptyList(),
    val error: String = "",
    val loading: Boolean = false
    )
