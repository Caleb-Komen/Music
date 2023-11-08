package com.techdroidcentre.home

import com.techdroidcentre.model.Album

data class HomeUiState(
    val topAlbums: List<Album> = emptyList(),
    val error: String = "",
    val loading: Boolean = false
)
