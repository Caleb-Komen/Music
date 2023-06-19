package com.techdroidcentre.albumdetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import com.techdroidcentre.albumdetails.navigation.AlbumDetailsArgs
import com.techdroidcentre.common.MusicServiceConnection
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlbumDetailsViewModel @Inject constructor(
    private val musicServiceConnection: MusicServiceConnection,
    private val savedStateHandle: SavedStateHandle
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

        val isPrepared = player.playbackState != Player.STATE_IDLE
        val playlistId = savedStateHandle.get<String>(PLAYLIST_ID)
        if (isPrepared && playlistId == checkNotNull(albumDetailArgs.albumId)) {
            when {
                !player.isPlaying -> player.play()
                player.playbackState == Player.STATE_ENDED -> player.seekTo(C.TIME_UNSET)
            }
        } else {
            savedStateHandle[PLAYLIST_ID] = checkNotNull(albumDetailArgs.albumId)
            val playlist: MutableList<MediaItem> = musicServiceConnection.getChildren(checkNotNull(albumDetailArgs.albumId))
                .toMutableList()
            player.setMediaItems(playlist, 0, C.TIME_UNSET)
            player.prepare()
            player.play()
        }
    }

    fun shuffle() {
        // TODO
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

    companion object {
        const val PLAYLIST_ID = "playlistId"
    }
}