package com.techdroidcentre.nowplaying

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
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

    private val handler = Handler(Looper.getMainLooper())

    init {
        musicServiceConnection.nowPlaying.onEach { mediaItem ->
            if (mediaItem != MediaItem.EMPTY) {
                _uiState.update {
                    it.copy(
                        song = mediaItem.toSong()
                    )
                }
                fetchPlaylistItems()
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

    fun play(id: String) {
        val player = musicServiceConnection.mediaBrowser.value ?: return
        val nowPlaying = musicServiceConnection.nowPlaying.value
        val isPrepared = player.playbackState != Player.STATE_IDLE
        val playlist = getPlaylistItems()
        if (isPrepared && id == nowPlaying.mediaId) {
            when {
                player.isPlaying -> player.pause()
                player.playbackState == Player.STATE_ENDED -> player.seekTo(C.TIME_UNSET)
                else -> player.play()
            }

        } else if (isPrepared) {
            val mediaItem = playlist.first { it.mediaId == id }
            player.seekTo(playlist.indexOf(mediaItem), C.TIME_UNSET)
            if (!player.isPlaying) player.play()
        } else {
            val mediaItem = playlist.first { it.mediaId == id }
            val startIndex = playlist.indexOf(mediaItem)
            player.setMediaItems(playlist, startIndex, C.TIME_UNSET)
            player.prepare()
            player.play()
        }
    }

    fun playOrPause() {
        val player = musicServiceConnection.mediaBrowser.value ?: return
        if (player.isPlaying) {
            player.pause()
        } else {
            // When player's state is Player.STATE_ENDED, call Player.seekTo to play the last
            // media item before the player transitioned to Player.STATE_ENDED.
            if (player.playbackState == Player.STATE_ENDED) {
                player.seekTo(0)
                updatePosition()
                return
            }
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
        // When player's state is Player.STATE_ENDED, set playWhenReady to false to prevent
        // the player from playing immediately.
        if (player.playbackState == Player.STATE_ENDED) {
            player.playWhenReady = false
            updatePosition()
        }
        player.seekTo(position)
    }

    fun togglePlaylistItems() {
        _uiState.update {
            it.copy(showPlaylistItems = !it.showPlaylistItems)
        }
    }

    private fun fetchPlaylistItems() {
        val playlist = getPlaylistItems()
        _uiState.update {
            it.copy(playlistItems = playlist.map { mediaItem -> mediaItem.toSong() })
        }
    }

    private fun getPlaylistItems(): List<MediaItem> {
        val player = musicServiceConnection.mediaBrowser.value ?: return emptyList()
        val playlist: MutableList<MediaItem> = mutableListOf()
        var windowIndex = player.currentTimeline.getFirstWindowIndex(player.shuffleModeEnabled)
        for (index in 0 until player.currentTimeline.windowCount) {
            playlist.add(player.getMediaItemAt(windowIndex))
            windowIndex = player.currentTimeline.getNextWindowIndex(windowIndex, player.repeatMode, player.shuffleModeEnabled)
        }
        return playlist
    }
}
