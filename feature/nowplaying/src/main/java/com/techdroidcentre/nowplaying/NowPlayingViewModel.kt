package com.techdroidcentre.nowplaying

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import com.techdroidcentre.common.MusicServiceConnection
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

    private val handler = Handler(Looper.getMainLooper())

    init {
        musicServiceConnection.nowPlaying.onEach { mediaItem ->
            if (mediaItem != MediaItem.EMPTY) {
                _uiState.update {
                    it.copy(
                        song = mediaItem.toSong()
                    )
                }
            }
        }.launchIn(viewModelScope)
        musicServiceConnection.duration.onEach { duration ->
            _uiState.update {
                it.copy(
                    duration = duration
                )
            }
            updatePosition()
        }.launchIn(viewModelScope)
        musicServiceConnection.isPlaying.onEach { isPlaying ->
            _uiState.update {
                it.copy(isPlaying = isPlaying)
            }
        }.launchIn(viewModelScope)
    }

    private fun updatePosition() {
        val player = musicServiceConnection.mediaBrowser.value ?: return
        handler.postDelayed(object: Runnable{
            override fun run() {
                if (player.currentPosition == player.duration) {
                    _uiState.update {
                        it.copy(
                            position = 0L
                        )
                    }
                    return
                }
                _uiState.update {
                    it.copy(
                        position = player.currentPosition
                    )
                }
                handler.postDelayed(this, 100L)
            }
        }, 100L)
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

    fun seekTo(position: Long) {
        val player = musicServiceConnection.mediaBrowser.value ?: return
        player.seekTo(position)
    }
}
