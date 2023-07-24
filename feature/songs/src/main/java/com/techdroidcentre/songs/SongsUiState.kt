package com.techdroidcentre.songs

import com.techdroidcentre.model.Song

data class SongsUiState(
    val songs: List<Song> = emptyList(),
    val playingSongId: String = "",
    val error: String = "",
    val loading: Boolean = false,
    val isSongPlaying: Boolean = false
)
