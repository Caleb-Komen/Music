package com.techdroidcentre.playlists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techdroidcentre.data.repository.PlaylistsRepository
import com.techdroidcentre.model.Playlist
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaylistsViewModel @Inject constructor(
    private val playlistsRepository: PlaylistsRepository
): ViewModel() {
    private val _uiState = MutableStateFlow(PlaylistsUiState())
    val uiState: StateFlow<PlaylistsUiState> = _uiState.asStateFlow()

    init {
        fetchPlaylists()
    }

    private fun fetchPlaylists() {
        _uiState.update {
            it.copy(loading = true)
        }
        val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
            _uiState.update {
                it.copy(
                    playlists = emptyList(),
                    error = throwable.localizedMessage ?: "Unknown Error",
                    loading = false
                )
            }
        }
        viewModelScope.launch(coroutineExceptionHandler) {
            playlistsRepository.getPlaylists().collect { playlists ->
                _uiState.update {
                    it.copy(
                        playlists = playlists,
                        error = "",
                        loading = false
                    )
                }
            }
        }
    }

    fun savePlaylist(name: String) {
        viewModelScope.launch {
            playlistsRepository.insertPlaylist(Playlist(0, name))
        }
    }

    fun updatePlaylist(playlist: Playlist) {
        viewModelScope.launch {
            playlistsRepository.updatePlaylist(playlist)
        }
    }

    fun deletePlaylist(id: Long) {
        viewModelScope.launch {
            playlistsRepository.deletePlaylist(id)
        }
    }
}