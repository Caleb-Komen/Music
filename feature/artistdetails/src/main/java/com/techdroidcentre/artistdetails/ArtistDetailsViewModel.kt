package com.techdroidcentre.artistdetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techdroidcentre.artistdetails.navigation.ArtistDetailsArg
import com.techdroidcentre.common.MusicServiceConnection
import com.techdroidcentre.common.toAlbum
import com.techdroidcentre.common.toArtist
import com.techdroidcentre.data.datastore.ArtistAlbumsSortOption
import com.techdroidcentre.data.datastore.MusicDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArtistDetailsViewModel @Inject constructor(
    private val musicServiceConnection: MusicServiceConnection,
    private val musicDataStore: MusicDataStore,
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

        combine(
            musicServiceConnection.mediaBrowser,
            musicDataStore.getArtistAlbumsSortOption()
        ) { browser, sortOption ->
            browser to sortOption
        }.onEach { (browser, sortOption) ->
            if (browser == null) return@onEach
            val mediaItem = musicServiceConnection.getMediaItem(checkNotNull(artistDetailArg.artistId))
            val children = musicServiceConnection.getChildren(checkNotNull(artistDetailArg.artistId))
                .map { item -> item.toAlbum() }
            val albums = when (sortOption) {
                ArtistAlbumsSortOption.TITLE -> children.sortedBy { it.name }
                ArtistAlbumsSortOption.YEAR_ASCENDING -> children.sortedBy { it.year }
                ArtistAlbumsSortOption.YEAR_DESCENDING -> children.sortedByDescending { it.year }
            }
            _uiState.update {
                it.copy(
                    artist = mediaItem.toArtist().name,
                    albums = albums,
                    error = "",
                    loading = false,
                    sortOption = sortOption
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

    fun setArtistAlbumsSortOption(artistAlbumsSortOption: ArtistAlbumsSortOption) {
        viewModelScope.launch {
            musicDataStore.setArtistAlbumsSortOption(artistAlbumsSortOption)
        }
    }
}