package com.techdroidcentre.favourites

import android.media.MediaMetadataRetriever
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.C
import androidx.media3.common.Player
import com.techdroidcentre.common.MusicServiceConnection
import com.techdroidcentre.data.datastore.FavouriteSongsSortOption
import com.techdroidcentre.data.datastore.MusicDataStore
import com.techdroidcentre.data.datastore.ShuffleMode
import com.techdroidcentre.data.repository.DefaultSongsRepository
import com.techdroidcentre.data.repository.FavouriteSongsRepository
import com.techdroidcentre.data.repository.SongsRepository
import com.techdroidcentre.model.Song
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavouriteSongsViewModel @Inject constructor(
    private val favouriteSongsRepository: FavouriteSongsRepository,
    private val songsRepository: SongsRepository,
    private val musicServiceConnection: MusicServiceConnection,
    private val musicDataStore: MusicDataStore
): ViewModel() {
    private val _uiState = MutableStateFlow(FavouriteSongsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        fetchFavouriteSongs()
    }

    private fun fetchFavouriteSongs() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(loading = true)
            }
            combine(
                favouriteSongsRepository.getFavouriteSongs(),
                musicDataStore.getFavouriteSongsSortOption()
            ) { favouriteSongs, sortOption ->
                val favSongs = favouriteSongs.map {
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
                val songs = when (sortOption) {
                    FavouriteSongsSortOption.TITLE -> favSongs.sortedBy { it.title }
                    FavouriteSongsSortOption.ARTIST -> favSongs.sortedBy { it.artist }
                }
                _uiState.update {
                    it.copy(songs = songs, error = "", loading = false, sortOption = sortOption)
                }
            }.launchIn(viewModelScope)
        }
    }

    fun play() {
        val player = musicServiceConnection.mediaBrowser.value ?: return
        val favourites = _uiState.value.songs.map {
            musicServiceConnection.getMediaItem(it.id.toString())
        }.toMutableList()
        player.setMediaItems(favourites)
        setShuffleMode(ShuffleMode.OFF)
        player.prepare()
        player.play()
    }

    fun shuffle() {
        val player = musicServiceConnection.mediaBrowser.value ?: return
        val favourites = _uiState.value.songs.map {
            musicServiceConnection.getMediaItem(it.id.toString())
        }.toMutableList()
        player.setMediaItems(favourites)
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
            val favourites = _uiState.value.songs.map {
                musicServiceConnection.getMediaItem(it.id.toString())
            }.toMutableList()
            val mediaItem = favourites.first { it.mediaId == id }
            val startIndex = favourites.indexOf(mediaItem)
            player.setMediaItems(favourites, startIndex, C.TIME_UNSET)
            player.prepare()
            player.play()
        }
    }

    fun removeSong(songId: Long) {
        viewModelScope.launch {
            favouriteSongsRepository.toggleFavourite(songId)
        }
    }

    fun setFavouriteSongsSortOption(favouriteSongsSortOption: FavouriteSongsSortOption) {
        viewModelScope.launch {
            musicDataStore.setFavouriteSongsSortOption(favouriteSongsSortOption)
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