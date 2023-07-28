package com.techdroidcentre.playlists

import com.techdroidcentre.model.Playlist

data class PlaylistsUiState(
    val playlists: List<Playlist> = emptyList(),
    val error: String = "",
    val loading: Boolean = false
)
