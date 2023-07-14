package com.techdroidcentre.artists

import com.techdroidcentre.model.Artist

data class ArtistsUiState(
    val artists: List<Artist> = emptyList(),
    val error: String = "",
    val loading: Boolean = false
)
