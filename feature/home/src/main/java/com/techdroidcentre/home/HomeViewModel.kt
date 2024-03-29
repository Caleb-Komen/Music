package com.techdroidcentre.home

import android.media.MediaMetadataRetriever
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.C
import androidx.media3.common.Player
import com.techdroidcentre.common.MusicServiceConnection
import com.techdroidcentre.data.mapper.toModel
import com.techdroidcentre.data.repository.AlbumsRepository
import com.techdroidcentre.data.repository.DefaultAlbumsRepository
import com.techdroidcentre.data.repository.DefaultSongsRepository
import com.techdroidcentre.data.repository.RecentlyPlayedRepository
import com.techdroidcentre.data.repository.SongsRepository
import com.techdroidcentre.data.repository.TopAlbumsRepository
import com.techdroidcentre.model.Song
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val topAlbumsRepository: TopAlbumsRepository,
    private val albumsRepository: AlbumsRepository,
    private val recentlyPlayedRepository: RecentlyPlayedRepository,
    private val songsRepository: SongsRepository,
    private val musicServiceConnection: MusicServiceConnection
): ViewModel(){
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    init {
        fetchTopAlbums()
        fetchRecentlyPlayed()
    }

    private fun fetchTopAlbums() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(loading = true)
            }
        topAlbumsRepository.getTopAlbums()
            .collect { topAlbums ->
                val albums = topAlbums.map {
                    (albumsRepository as DefaultAlbumsRepository).albums.first {
                            album -> it.albumId == album.id
                    }.toModel()
                }.take(10)
                _uiState.update {
                    it.copy(topAlbums = albums, error = "", loading = false)
                }
            }
        }
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
                    }.take(10).map {
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