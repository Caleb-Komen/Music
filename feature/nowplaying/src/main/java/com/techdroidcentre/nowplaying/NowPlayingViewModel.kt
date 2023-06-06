package com.techdroidcentre.nowplaying

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import com.techdroidcentre.common.MusicServiceConnection
import com.techdroidcentre.model.Song
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class NowPlayingViewModel @Inject constructor(
    private val musicServiceConnection: MusicServiceConnection
): ViewModel() {
    private val _uiState = MutableStateFlow(NowPlayingUiState())
    val uiState: StateFlow<NowPlayingUiState> = _uiState

    init {
        musicServiceConnection.nowPlaying.onEach { mediaItem ->
            if (mediaItem != MediaItem.EMPTY) {
                _uiState.update {
                    it.copy(song = mediaItem.toSong())
                }
            }
        }.launchIn(viewModelScope)
        musicServiceConnection.isPlaying.onEach { isPlaying ->
            _uiState.update {
                it.copy(isPlaying = isPlaying)
            }
        }.launchIn(viewModelScope)
    }

    fun playOrPause() {
        val player = musicServiceConnection.mediaBrowser.value ?: return
        if (player.isPlaying) {
            player.pause()
        } else {
            player.play()
        }
    }

    fun playNextSong() {
        val player = musicServiceConnection.mediaBrowser.value ?: return
        player.seekToNextMediaItem()
    }

    fun playPreviousSong() {
        val player = musicServiceConnection.mediaBrowser.value ?: return
        player.seekToPreviousMediaItem()
    }
}

data class NowPlayingUiState(
    val isPlaying: Boolean = false,
    val song: Song = Song(title = "Not Playing")
)

fun MediaItem.toSong(): Song {
    return Song(
        id = mediaId.toLong(),
        uri = localConfiguration?.uri.toString(),
        title = mediaMetadata.title.toString(),
        album = mediaMetadata.albumTitle.toString(),
        artist = mediaMetadata.artist.toString(),
        trackNumber = mediaMetadata.trackNumber!!,
        artworkUri = mediaMetadata.artworkUri.toString(),
        artworkData = mediaMetadata.artworkData
    )
}