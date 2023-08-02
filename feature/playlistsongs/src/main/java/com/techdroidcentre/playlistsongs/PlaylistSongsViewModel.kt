package com.techdroidcentre.playlistsongs

import android.os.Bundle
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.session.MediaBrowser
import com.techdroidcentre.common.MusicServiceConnection
import com.techdroidcentre.common.toSong
import com.techdroidcentre.data.datastore.MusicDataStore
import com.techdroidcentre.data.datastore.PlaylistSongsSortOption
import com.techdroidcentre.data.repository.PlaylistSongsRepository
import com.techdroidcentre.data.repository.PlaylistsRepository
import com.techdroidcentre.player.MusicService
import com.techdroidcentre.playlistsongs.navigation.PlaylistSongsArgs
import com.techdroidcentre.playlistsongs.util.addOrRemove
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaylistSongsViewModel @Inject constructor(
    private val playlistsRepository: PlaylistsRepository,
    private val playlistSongsRepository: PlaylistSongsRepository,
    private val musicServiceConnection: MusicServiceConnection,
    private val musicDataStore: MusicDataStore,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _uiState = MutableStateFlow(PlaylistSongsUiState())
    val uiState: StateFlow<PlaylistSongsUiState> = _uiState.asStateFlow()

    private val playlistSongsArgs = PlaylistSongsArgs(savedStateHandle)

    private var mediaBrowser: MediaBrowser? = null

    init {
        fetchPlaylist()
        fetchPlaylistSongs()
        fetchAllSongs()
    }

    private fun fetchPlaylist() {
        viewModelScope.launch {
            playlistsRepository.getPlaylist(checkNotNull(playlistSongsArgs.playlistId)).collect { playlist ->
                _uiState.update {
                    it.copy(playlist = playlist)
                }
            }
        }
    }

    private fun fetchPlaylistSongs() {
        _uiState.update {
            it.copy(loading = true)
        }
        val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
            _uiState.update {
                it.copy(
                    playlistSongs = emptyList(),
                    error = throwable.localizedMessage ?: "Unknown Error",
                    loading = false
                )
            }
        }
        viewModelScope.launch(coroutineExceptionHandler) {
            combine(
                playlistSongsRepository.getPlaylistSongs(checkNotNull(playlistSongsArgs.playlistId)),
                musicDataStore.getPlaylistSongsSortOption()
            ) { songs, playlistSongsSortOption ->
                songs to playlistSongsSortOption
            }.collect { (playlistSongs, playlistSongsSortOption) ->
                val songs = when (playlistSongsSortOption) {
                    PlaylistSongsSortOption.TITLE -> playlistSongs.sortedBy { it.title }
                    PlaylistSongsSortOption.ARTIST -> playlistSongs.sortedBy { it.artist }
                }
                _uiState.update {
                    it.copy(
                        playlistSongs = songs,
                        error = "",
                        loading = false,
                        sortOption = playlistSongsSortOption
                    )
                }
            }
        }
    }

    private fun fetchAllSongs() {
        viewModelScope.launch {
            musicServiceConnection.mediaBrowser.collect { browser ->
                this@PlaylistSongsViewModel.mediaBrowser = browser ?: return@collect
                val children = getAllSongs()
                _uiState.update {
                    it.copy(allSongs = children.map { mediaItem -> mediaItem.toSong() })
                }
            }
        }
    }
    private fun getAllSongs(): List<MediaItem> {
        return musicServiceConnection.getChildren(checkNotNull(playlistSongsArgs.songsRoot))
    }

    fun insertSongs() {
        viewModelScope.launch {
            val playlistId = _uiState.value.playlist.id
            val ids = _uiState.value.selectedSongs
            val songs = uiState.value.allSongs.filter { ids.contains(it.id) }
            playlistSongsRepository.insertPlaylistSongs(playlistId, *songs.toTypedArray())
        }
    }

    fun updateSelectedSongs(songId: Long, clean: Boolean = false) {
        if (clean) {
            _uiState.update {
                it.copy(selectedSongs = emptySet())
            }
            return
        }
        _uiState.update {
            val ids = it.selectedSongs.toMutableSet()
            ids.addOrRemove(songId)
            it.copy(selectedSongs = ids)
        }
    }

    fun removeSong(songId: Long) {
        viewModelScope.launch {
            playlistSongsRepository.deletePlaylistSong(songId)
        }
    }

    fun play() {
        val player = musicServiceConnection.mediaBrowser.value ?: return
        val ids = _uiState.value.playlistSongs.map { it.id.toString() }
        val playlist: MutableList<MediaItem> = getAllSongs().filter { ids.contains(it.mediaId) }.toMutableList()
        player.setMediaItems(playlist)
        player.sendCustomCommand(MusicService.COMMAND_SHUFFLE_MODE_OFF, Bundle.EMPTY)
        player.prepare()
        player.play()
    }

    fun shuffle() {
        val player = musicServiceConnection.mediaBrowser.value ?: return
        val ids = _uiState.value.playlistSongs.map { it.id.toString() }
        val playlist: MutableList<MediaItem> = getAllSongs().filter { ids.contains(it.mediaId) }.toMutableList()
        player.setMediaItems(playlist)
        player.sendCustomCommand(MusicService.COMMAND_SHUFFLE_MODE_ON, Bundle.EMPTY)
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
            val ids = _uiState.value.playlistSongs.map { it.id.toString() }
            val playlist: MutableList<MediaItem> = getAllSongs().filter { ids.contains(it.mediaId) }.toMutableList()
            val mediaItem = playlist.first { it.mediaId == id }
            val startIndex = playlist.indexOf(mediaItem)
            player.setMediaItems(playlist, startIndex, C.TIME_UNSET)
            player.prepare()
            player.play()
        }
    }

    fun setSongsSortOption(playlistSongsSortOption: PlaylistSongsSortOption) {
        viewModelScope.launch {
            musicDataStore.setPlaylistSongsSortOption(playlistSongsSortOption)
        }
    }
}