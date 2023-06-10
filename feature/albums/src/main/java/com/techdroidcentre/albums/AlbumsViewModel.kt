package com.techdroidcentre.albums

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.techdroidcentre.common.MusicServiceConnection
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

class AlbumsViewModel constructor(
    private val musicServiceConnection: MusicServiceConnection,
    private val albumsId: String
): ViewModel() {
    private val _uiState = MutableStateFlow(AlbumsUiState())
    val uiState: StateFlow<AlbumsUiState> = _uiState

    init {
        fetchAlbums()
    }

    private fun fetchAlbums() {
        _uiState.update {
            it.copy(loading = true)
        }
        musicServiceConnection.mediaBrowser.onEach { browser ->
            if (browser == null) return@onEach
            val mediaItems = musicServiceConnection.getChildren(albumsId)
            _uiState.update {
                it.copy(
                    albums = mediaItems.map { mediaItem -> mediaItem.toAlbum() },
                    error = "",
                    loading = false
                )
            }
            Log.d("AlbumsViewModel", "Uri -> ${_uiState.value.albums[0].artworkUri}")
            Log.d("AlbumsViewModel", "Uri -> content://media/external/audio/albumart/${_uiState.value.albums[0].id.toString()}")
        }.catch { throwable ->
            _uiState.update {
                it.copy(
                    albums = emptyList(),
                    error = throwable.localizedMessage ?: "Unknown Error",
                    loading = false
                )
            }
        }.launchIn(viewModelScope)
    }

    companion object {
        fun provideFactory(albumsId: String) = viewModelFactory {
            initializer {
                val context = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Context).applicationContext
                AlbumsViewModel(MusicServiceConnection(context), albumsId)
            }
        }
    }
}