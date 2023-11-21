package com.techdroidcentre.recentlyplayed

import com.techdroidcentre.model.Song

data class RecentlyPlayedUiState(
    val recentlyPlayed: List<Song> = emptyList(),
    val error: String = "",
    val loading: Boolean = false
)
