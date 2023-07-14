package com.techdroidcentre.songs

import com.techdroidcentre.model.Song

data class SongsUiState(
    val songs: List<Song> = emptyList(),
    val error: String = "",
    val loading: Boolean = false,
)
