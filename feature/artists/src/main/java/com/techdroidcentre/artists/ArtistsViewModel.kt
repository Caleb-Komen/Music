package com.techdroidcentre.artists

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.techdroidcentre.common.MusicServiceConnection
import com.techdroidcentre.common.toArtist
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

class ArtistsViewModel(
    private val musicServiceConnection: MusicServiceConnection,
    private val artistsId: String
): ViewModel() {
    private val _uiState = MutableStateFlow(ArtistsUiState())
    val uiState: StateFlow<ArtistsUiState> = _uiState

    init {
        fetchArtists()
    }

    private fun fetchArtists() {
        _uiState.update {
            it.copy(loading = true)
        }
        musicServiceConnection.mediaBrowser.onEach { browser ->
            if (browser == null) return@onEach
            val children = musicServiceConnection.getChildren(artistsId)
            _uiState.update {
                it.copy(
                    artists = children.map { mediaItem -> mediaItem.toArtist() },
                    error = "",
                    loading = false
                )
            }
        }.catch { throwable ->
            _uiState.update {
                it.copy(
                    artists = emptyList(),
                    error = throwable.localizedMessage ?: "Unknown Error.",
                    loading = false
                )
            }
        }.launchIn(viewModelScope)
    }

    companion object {
        fun provideFactory(artistsId: String) = viewModelFactory {
            initializer {
                val context = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Context).applicationContext
                ArtistsViewModel(MusicServiceConnection(context), artistsId)
            }
        }
    }
}