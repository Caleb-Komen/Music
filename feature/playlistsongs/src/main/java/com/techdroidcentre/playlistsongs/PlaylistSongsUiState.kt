package com.techdroidcentre.playlistsongs

import com.techdroidcentre.model.Playlist
import com.techdroidcentre.model.Song

data class PlaylistSongsUiState(
    val playlist: Playlist = Playlist(-1L, ""),
    val playlistSongs: List<Song> = emptyList(),
    val allSongs: List<Song> = emptyList(),
    val selectedSongs: Set<Long> = emptySet(),
    val playingSongId: String = "",
    val error: String = "",
    val loading: Boolean = false,
    val isSongPlaying: Boolean= false
)

data class SelectableSong(
    val song: Song = Song(),
    val selected: Boolean = false
)
