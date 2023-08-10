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
import com.techdroidcentre.common.toSong
import com.techdroidcentre.data.datastore.MusicDataStore
import com.techdroidcentre.data.datastore.SongsSortOption
import com.techdroidcentre.model.Song
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SongsViewModel(
    private val musicServiceConnection: MusicServiceConnection,
    private val songsId: String,
    private val musicDataStore: MusicDataStore
): ViewModel() {
    private val _uiState = MutableStateFlow(SongsUiState())
    val uiState: StateFlow<SongsUiState> = _uiState

    private var mediaBrowser: MediaBrowser? = null

    init {
        fetchSongs(songsId)
        fetchCurrentlyPlayingSong()
        fetchPlayingState()
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
            val playlist: List<MediaItem> = musicServiceConnection.getChildren(songsId)
            val mediaItems = when (_uiState.value.sortOption) {
                SongsSortOption.TITLE -> playlist.sortedBy { mediaItem -> mediaItem.mediaMetadata.title.toString() }
                SongsSortOption.ARTIST -> playlist.sortedBy { mediaItem -> mediaItem.mediaMetadata.artist.toString() }
            }.toMutableList()
            val mediaItem = mediaItems.first { it.mediaId == song.id.toString() }
            if (mediaItems.isEmpty()) mediaItems.add(mediaItem)
            val indexOf = mediaItems.indexOf(mediaItem)
            val startIndex = if (indexOf >=0 ) indexOf else 0
            player.setMediaItems(mediaItems, startIndex, C.TIME_UNSET)
            player.prepare()
            player.play()
        }
    }

    private fun fetchSongs(songsId: String) {
        _uiState.update {
            it.copy(loading = true)
        }

        viewModelScope.launch {
            combine(
                musicServiceConnection.mediaBrowser,
                musicDataStore.getSongsSortOption()
            ) { browser, songsSortOption ->
                browser to songsSortOption
            }.catch { throwable ->
                _uiState.update {
                    it.copy(
                        songs = emptyList(),
                        error = throwable.localizedMessage ?: "Unknown Error",
                        loading = false
                    )
                }
            }.collect { (browser, songsSortOption) ->
                this@SongsViewModel.mediaBrowser = browser ?: return@collect
                val children = musicServiceConnection.getChildren(songsId).map { mediaItem -> mediaItem.toSong() }
                val songs = when (songsSortOption) {
                    SongsSortOption.TITLE -> children.sortedBy { song -> song.title }
                    SongsSortOption.ARTIST -> children.sortedBy { song -> song.artist }
                }
                _uiState.update {
                    it.copy(
                        songs = songs,
                        error = "",
                        loading = false,
                        sortOption = songsSortOption
                    )
                }
            }
        }

    }

    private fun fetchCurrentlyPlayingSong() {
        viewModelScope.launch {
            musicServiceConnection.nowPlaying.collect { mediaItem ->
                if (mediaItem != MediaItem.EMPTY) {
                    _uiState.update {
                        it.copy(playingSongId = mediaItem.mediaId)
                    }
                }
            }
        }
    }

    private fun fetchPlayingState() {
        viewModelScope.launch {
            musicServiceConnection.isPlaying.collect { isPlaying ->
                _uiState.update {
                    it.copy(isSongPlaying = isPlaying)
                }
            }
        }
    }

    fun setSongsSortOption(songsSortOption: SongsSortOption) {
        viewModelScope.launch {
            musicDataStore.setSongsSortOption(songsSortOption)
        }
    }

    override fun onCleared() {
        super.onCleared()
        musicServiceConnection.releaseBrowser()
    }

    companion object {
        fun provideFactory(songsId: String) = viewModelFactory {
            initializer {
                val context = (this[APPLICATION_KEY] as Context).applicationContext
                SongsViewModel(MusicServiceConnection(context), songsId, MusicDataStore(context))
            }
        }
    }
}
