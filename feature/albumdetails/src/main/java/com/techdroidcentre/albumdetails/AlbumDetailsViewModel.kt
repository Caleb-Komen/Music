package com.techdroidcentre.albumdetails

import android.os.Bundle
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import com.techdroidcentre.albumdetails.navigation.AlbumDetailsArgs
import com.techdroidcentre.common.MusicServiceConnection
import com.techdroidcentre.common.toAlbum
import com.techdroidcentre.common.toSong
import com.techdroidcentre.data.datastore.MusicDataStore
import com.techdroidcentre.data.datastore.ShuffleMode
import com.techdroidcentre.player.MusicService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlbumDetailsViewModel @Inject constructor(
    private val musicServiceConnection: MusicServiceConnection,
    private val musicDataStore: MusicDataStore,
    savedStateHandle: SavedStateHandle
): ViewModel() {
    private val _uiState = MutableStateFlow(AlbumDetailsUiState())
    val uiState: StateFlow<AlbumDetailsUiState> = _uiState

    private val albumDetailArgs = AlbumDetailsArgs(savedStateHandle)

    init {
        viewModelScope.launch {
            _uiState.update {
                it.copy(loading = true)
            }
            musicServiceConnection.mediaBrowser.collect { browser ->
                if (browser == null) return@collect
                val item = musicServiceConnection.getMediaItem(checkNotNull(albumDetailArgs.albumId))
                val children = musicServiceConnection.getChildren(checkNotNull(albumDetailArgs.albumId))
                _uiState.update {
                    it.copy(
                        album = item.toAlbum(),
                        songs = children.map { mediaItem -> mediaItem.toSong() },
                        loading = false
                    )
                }
            }
        }
    }

    fun play() {
        val player = musicServiceConnection.mediaBrowser.value ?: return
        val playlist: MutableList<MediaItem> = musicServiceConnection.getChildren(checkNotNull(albumDetailArgs.albumId))
            .toMutableList()
        player.setMediaItems(playlist)
        player.sendCustomCommand(MusicService.COMMAND_SHUFFLE_MODE_OFF, Bundle.EMPTY)
        setShuffleMode(ShuffleMode.OFF)
        player.prepare()
        player.play()
    }

    fun shuffle() {
        val player = musicServiceConnection.mediaBrowser.value ?: return
        val playlist: MutableList<MediaItem> = musicServiceConnection.getChildren(checkNotNull(albumDetailArgs.albumId))
            .toMutableList()
        player.setMediaItems(playlist)
        player.sendCustomCommand(MusicService.COMMAND_SHUFFLE_MODE_ON, Bundle.EMPTY)
        setShuffleMode(ShuffleMode.ON)
        player.prepare()
        player.play()
    }

    fun playOrPause(id: String) {
        val player = musicServiceConnection.mediaBrowser.value ?: return

        val nowPlaying = musicServiceConnection.nowPlaying.value
        val isPrepared = player.playbackState != Player.STATE_IDLE
        if (isPrepared && id == nowPlaying.mediaId) {
            when {
                player.isPlaying -> player.pause()
                player.playbackState == Player.STATE_ENDED -> player.seekTo(C.TIME_UNSET)
                else -> player.play()
            }
        } else {
            val playlist: MutableList<MediaItem> = musicServiceConnection.getChildren(checkNotNull(albumDetailArgs.albumId))
                .toMutableList()
            val mediaItem = playlist.first { it.mediaId == id }
            val startIndex = playlist.indexOf(mediaItem)
            player.setMediaItems(playlist, startIndex, C.TIME_UNSET)
            player.prepare()
            player.play()
        }
    }

    private fun setShuffleMode(shuffleMode: ShuffleMode) {
        viewModelScope.launch {
            musicDataStore.setShuffleMode(shuffleMode)
        }
    }
}