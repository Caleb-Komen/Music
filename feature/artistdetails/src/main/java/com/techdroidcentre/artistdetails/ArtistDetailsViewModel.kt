package com.techdroidcentre.artistdetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techdroidcentre.artistdetails.navigation.ArtistDetailsArg
import com.techdroidcentre.common.MusicServiceConnection
import com.techdroidcentre.common.toAlbum
import com.techdroidcentre.common.toArtist
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class ArtistDetailsViewModel @Inject constructor(
    private val musicServiceConnection: MusicServiceConnection,
    savedStateHandle: SavedStateHandle
): ViewModel() {
    private val _uiState = MutableStateFlow(ArtistDetailsUiState())
    val uiState: StateFlow<ArtistDetailsUiState> = _uiState

    private val artistDetailArg = ArtistDetailsArg(savedStateHandle)

    init {
        fetchArtistDetails()
    }

    private fun fetchArtistDetails() {
        _uiState.update { it.copy(loading = true) }
        musicServiceConnection.mediaBrowser.onEach { browser ->
            if (browser == null) return@onEach
            val mediaItem = musicServiceConnection.getMediaItem(checkNotNull(artistDetailArg.artistId))
            val children = musicServiceConnection.getChildren(checkNotNull(artistDetailArg.artistId))
            _uiState.update {
                it.copy(
                    artist = mediaItem.toArtist().name,
                    albums = children.map { mediaItem -> mediaItem.toAlbum() },
                    error = "",
                    loading = false
                )
            }
        }.catch { throwable ->
            _uiState.update {
                it.copy(
                    artist = "",
                    albums = emptyList(),
                    error = throwable.localizedMessage ?: "Unknown Error.",
                    loading = false
                )
            }
        }.launchIn(viewModelScope)
    }
}