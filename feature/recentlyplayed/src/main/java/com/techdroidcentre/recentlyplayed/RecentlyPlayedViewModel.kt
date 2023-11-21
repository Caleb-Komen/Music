package com.techdroidcentre.recentlyplayed

import android.media.MediaMetadataRetriever
import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.C
import androidx.media3.common.Player
import com.techdroidcentre.common.MusicServiceConnection
import com.techdroidcentre.data.datastore.MusicDataStore
import com.techdroidcentre.data.datastore.ShuffleMode
import com.techdroidcentre.data.repository.DefaultSongsRepository
import com.techdroidcentre.data.repository.RecentlyPlayedRepository
import com.techdroidcentre.data.repository.SongsRepository
import com.techdroidcentre.model.Song
import com.techdroidcentre.player.MusicService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecentlyPlayedViewModel @Inject constructor(
    private val recentlyPlayedRepository: RecentlyPlayedRepository,
    private val songsRepository: SongsRepository,
    private val musicServiceConnection: MusicServiceConnection,
    private val musicDataStore: MusicDataStore
): ViewModel() {
    private val _uiState = MutableStateFlow(RecentlyPlayedUiState())
    val uiState = _uiState.asStateFlow()

    init {
        fetchRecentlyPlayed()
    }

    private fun fetchRecentlyPlayed() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(loading = true)
            }
            recentlyPlayedRepository.getRecentlyPlayed()
                .collect { recentlyPlayed ->
                    val songs = recentlyPlayed.map {
                        (songsRepository as DefaultSongsRepository).songs.first { song ->
                            it.songId == song.id
                        }
                    }.map {
                        Song(
                            id = it.id,
                            uri = it.uri,
                            title = it.title,
                            album = it.album,
                            artist = it.artist,
                            trackNumber = it.trackNumber,
                            artworkUri = it.uri,
                            artworkData = getArtworkData(it.path)
                        )
                    }
                    _uiState.update {
                        it.copy(recentlyPlayed = songs, error = "", loading = false)
                    }
                }
        }
    }

    fun play() {
        val player = musicServiceConnection.mediaBrowser.value ?: return
        val recentlyPlayed = _uiState.value.recentlyPlayed.map {
            musicServiceConnection.getMediaItem(it.id.toString())
        }.toMutableList()
        player.setMediaItems(recentlyPlayed)
        player.sendCustomCommand(MusicService.COMMAND_SHUFFLE_MODE_OFF, Bundle.EMPTY)
        setShuffleMode(ShuffleMode.OFF)
        player.prepare()
        player.play()
    }

    fun shuffle() {
        val player = musicServiceConnection.mediaBrowser.value ?: return
        val recentlyPlayed = _uiState.value.recentlyPlayed.map {
            musicServiceConnection.getMediaItem(it.id.toString())
        }.toMutableList()
        player.setMediaItems(recentlyPlayed)
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
            val recentlyPlayed = _uiState.value.recentlyPlayed.map {
                musicServiceConnection.getMediaItem(it.id.toString())
            }.toMutableList()
            val mediaItem = recentlyPlayed.first { it.mediaId == id }
            val startIndex = recentlyPlayed.indexOf(mediaItem)
            player.setMediaItems(recentlyPlayed, startIndex, C.TIME_UNSET)
            player.prepare()
            player.play()
        }
    }

    private fun setShuffleMode(shuffleMode: ShuffleMode) {
        viewModelScope.launch {
            musicDataStore.setShuffleMode(shuffleMode)
        }
    }

    private fun getArtworkData(path: String): ByteArray? {
        val retriever = MediaMetadataRetriever()
        try {
            retriever.setDataSource(path)
        } catch (ex: Exception) {
            retriever.release()
            return null
        }
        val art = retriever.embeddedPicture
        retriever.release()
        return art
    }
}