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
import com.techdroidcentre.common.MusicServiceConnection
import com.techdroidcentre.model.Song
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

class SongsViewModel(
    private val musicServiceConnection: MusicServiceConnection,
    private val songsId: String
): ViewModel() {
    private val _uiState = MutableStateFlow(SongsUiState())
    val uiState: StateFlow<SongsUiState> = _uiState

    private var mediaBrowser: MediaBrowser? = null

    init {
        fetchSongs(songsId)
    }

    fun playOrPause(song: Song) {
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
            val playlist: MutableList<MediaItem> = musicServiceConnection.getChildren(songsId).toMutableList()
            val mediaItem = playlist.first { it.mediaId == song.id.toString() }
            if (playlist.isEmpty()) playlist.add(mediaItem)
            val indexOf = playlist.indexOf(mediaItem)
            val startIndex = if (indexOf >=0 ) indexOf else 0
            player.setMediaItems(playlist, startIndex, C.TIME_UNSET)
            player.prepare()
            player.play()
        }
    }

    private fun fetchSongs(songsId: String) {
        _uiState.update {
            it.copy(loading = true)
        }
        musicServiceConnection.mediaBrowser.onEach { browser ->
            this.mediaBrowser = browser ?: return@onEach
            val children = musicServiceConnection.getChildren(songsId)
            _uiState.update {
                it.copy(
                    songs = children.map { mediaItem -> mediaItem.toSong() },
                    error = "",
                    loading = false
                )
            }
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

    override fun onCleared() {
        super.onCleared()
        musicServiceConnection.releaseBrowser()
    }

    companion object {
        fun provideFactory(songsId: String) = viewModelFactory {
            initializer {
                val context = (this[APPLICATION_KEY] as Context).applicationContext
                SongsViewModel(MusicServiceConnection(context), songsId)
            }
        }
    }
}
