package com.techdroidcentre.songs

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.session.MediaBrowser
import androidx.media3.session.MediaController
import com.google.common.util.concurrent.MoreExecutors
import com.techdroidcentre.common.MusicServiceConnection
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

class SongsViewModel(
    private val musicServiceConnection: MusicServiceConnection,
    rootMediaItem: MediaItem
): ViewModel() {
    private val _uiState = MutableStateFlow(SongsUiState())
    val uiState: StateFlow<SongsUiState> = _uiState

    private var mediaBrowser: MediaBrowser? = null

    init {
        _uiState.update {
            it.copy(loading = true)
        }
        musicServiceConnection.mediaBrowser.onEach { browser ->
            this.mediaBrowser = browser
            val mediaBrowser = this.mediaBrowser ?: return@onEach
            fetchRootChildren(mediaBrowser, rootMediaItem)
        }.catch { throwable ->
            _uiState.update {
                it.copy(
                    songs = emptyList(),
                    error = throwable.localizedMessage ?: "Unknown Error",
                    loading = false
                )
            }
        }.launchIn(viewModelScope)

    }

    fun playOrPause(
        song: Song
    ) {
        val player = this.mediaBrowser ?: return
        val nowPlaying = musicServiceConnection.nowPlaying.value
        val isPrepared = player.playbackState != Player.STATE_IDLE

        if (isPrepared && song.id.toString() == nowPlaying.mediaId) {
            when {
                player.isPlaying -> player.pause()
                player.playbackState == Player.STATE_ENDED -> player.seekTo(C.TIME_UNSET)
                else -> player.play()
            }
        } else {
            val playlist: MutableList<MediaItem> = musicServiceConnection.getChildren("SONGS_ID").toMutableList()
            val mediaItem = playlist.first { it.mediaId == song.id.toString() }
            if (playlist.isEmpty()) playlist.add(mediaItem)
            val indexOf = playlist.indexOf(mediaItem)
            val startIndex = if (indexOf >=0 ) indexOf else 0
            player.setMediaItems(playlist, startIndex, C.TIME_UNSET)
            player.prepare()
            player.play()
        }
    }

    private fun fetchRootChildren(mediaBrowser: MediaBrowser, rootItem: MediaItem) {
        val childrenFuture = mediaBrowser.getChildren(rootItem.mediaId, 0, Int.MAX_VALUE, null)
        childrenFuture.addListener(
            {
                val result = childrenFuture.get()
                val children = result.value!!
                getSongs(mediaBrowser, children[0])
            },
            MoreExecutors.directExecutor()
        )
    }

    private fun getSongs(mediaBrowser: MediaBrowser, mediaItem: MediaItem) {
        val childrenFuture = mediaBrowser.getChildren(mediaItem.mediaId, 0, Int.MAX_VALUE, null)
        childrenFuture.addListener(
            {
                val result = childrenFuture.get()
                val children = result.value
                _uiState.update {
                    it.copy(
                        songs = children?.map { mediaItem -> mediaItem.toSong() } ?: emptyList(),
                        error = "",
                        loading = false
                    )
                }
            },
            MoreExecutors.directExecutor()
        )
    }

    override fun onCleared() {
        super.onCleared()
        musicServiceConnection.releaseBrowser()
    }

    companion object {
        fun provideFactory(rootMediaItem: MediaItem) = viewModelFactory {
            initializer {
                val context = (this[APPLICATION_KEY] as Context).applicationContext
                SongsViewModel(MusicServiceConnection(context), rootMediaItem)
            }
        }
    }
}
