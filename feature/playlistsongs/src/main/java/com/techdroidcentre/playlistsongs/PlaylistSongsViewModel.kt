package com.techdroidcentre.playlistsongs

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.session.MediaBrowser
import com.techdroidcentre.common.MusicServiceConnection
import com.techdroidcentre.common.toSong
import com.techdroidcentre.data.repository.PlaylistSongsRepository
import com.techdroidcentre.data.repository.PlaylistsRepository
import com.techdroidcentre.playlistsongs.navigation.PlaylistSongsArgs
import com.techdroidcentre.playlistsongs.util.addOrRemove
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaylistSongsViewModel @Inject constructor(
    private val playlistsRepository: PlaylistsRepository,
    private val playlistSongsRepository: PlaylistSongsRepository,
    private val musicServiceConnection: MusicServiceConnection,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _uiState = MutableStateFlow(PlaylistSongsUiState())
    val uiState: StateFlow<PlaylistSongsUiState> = _uiState.asStateFlow()

    private val playlistSongsArgs = PlaylistSongsArgs(savedStateHandle)

    private var mediaBrowser: MediaBrowser? = null

    init {
        fetchPlaylist()
        fetchPlaylistSongs()
        fetchAllSongs()
    }

    private fun fetchPlaylist() {
        viewModelScope.launch {
            playlistsRepository.getPlaylist(checkNotNull(playlistSongsArgs.playlistId)).collect { playlist ->
                _uiState.update {
                    it.copy(playlist = playlist)
                }
            }
        }
    }

    private fun fetchPlaylistSongs() {
        _uiState.update {
            it.copy(loading = true)
        }
        val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
            _uiState.update {
                it.copy(
                    playlistSongs = emptyList(),
                    error = throwable.localizedMessage ?: "Unknown Error",
                    loading = false
                )
            }
        }
        viewModelScope.launch(coroutineExceptionHandler) {
            playlistSongsRepository.getPlaylistSongs(checkNotNull(playlistSongsArgs.playlistId)).collect { songs ->
                _uiState.update {
                    it.copy(
                        playlistSongs = songs,
                        error = "",
                        loading = false
                    )
                }
            }
        }
    }

    private fun fetchAllSongs() {
        viewModelScope.launch {
            musicServiceConnection.mediaBrowser.collect { browser ->
                this@PlaylistSongsViewModel.mediaBrowser = browser ?: return@collect
                val children = musicServiceConnection.getChildren(checkNotNull(playlistSongsArgs.songsRoot))
                _uiState.update {
                    it.copy(allSongs = children.map { mediaItem -> mediaItem.toSong() })
                }
            }
        }
    }

    fun insertSongs() {
        viewModelScope.launch {
            val playlistId = _uiState.value.playlist.id
            val ids = _uiState.value.selectedSongs
            val songs = uiState.value.allSongs.filter { ids.contains(it.id) }
            playlistSongsRepository.insertPlaylistSongs(playlistId, *songs.toTypedArray())
        }
    }

    fun updateSelectedSongs(songId: Long, clean: Boolean = false) {
        if (clean) {
            _uiState.update {
                it.copy(selectedSongs = emptySet())
            }
            return
        }
        _uiState.update {
            val ids = it.selectedSongs.toMutableSet()
            ids.addOrRemove(songId)
            it.copy(selectedSongs = ids)
        }
    }

    fun removeSong(songId: Long) {
        viewModelScope.launch {
            playlistSongsRepository.deletePlaylistSong(songId)
        }
    }
}