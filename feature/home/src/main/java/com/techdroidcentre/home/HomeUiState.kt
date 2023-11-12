package com.techdroidcentre.home

import com.techdroidcentre.model.Album
import com.techdroidcentre.model.Song

data class HomeUiState(
    val topAlbums: List<Album> = emptyList(),
    val recentlyPlayed: List<Song> = emptyList(),
    val error: String = "",
    val loading: Boolean = false
)
