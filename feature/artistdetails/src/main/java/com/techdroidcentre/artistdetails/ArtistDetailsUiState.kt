package com.techdroidcentre.artistdetails

import com.techdroidcentre.model.Album

data class ArtistDetailsUiState(
    val artist: String = "",
    val albums: List<Album> = emptyList(),
    val error: String = "",
    val loading: Boolean = false
)

