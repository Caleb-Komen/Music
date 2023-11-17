package com.techdroidcentre.nowplaying

import com.techdroidcentre.data.datastore.RepeatMode
import com.techdroidcentre.model.Song

data class NowPlayingUiState(
    val isPlaying: Boolean = false,
    val song: Song = Song(title = "Not Playing"),
    val duration: Long = 0L,
    val position: Long = 0L,
    val showPlaylistItems: Boolean = false,
    val playlistItems: List<Song> = emptyList(),
    val shuffleModeEnabled: Boolean = false,
    val repeatMode: RepeatMode = RepeatMode.OFF,
    val isFavourite: Boolean = false
)
