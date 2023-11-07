package com.techdroidcentre.music

import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.session.MediaBrowser
import com.google.common.util.concurrent.MoreExecutors
import com.techdroidcentre.common.MusicServiceConnection
import com.techdroidcentre.data.datastore.MusicDataStore
import com.techdroidcentre.data.datastore.RepeatMode
import com.techdroidcentre.data.datastore.ShuffleMode
import com.techdroidcentre.player.MusicService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val musicServiceConnection: MusicServiceConnection,
    musicDataStore: MusicDataStore
): ViewModel() {
    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState

    init {
        combine(
            musicServiceConnection.mediaBrowser,
            musicDataStore.getShuffleMode(),
            musicDataStore.getRepeatMode()
        ) { browser, shuffleMode, repeatMode ->
            val mediaBrowser: MediaBrowser = browser ?: return@combine
            val rootFuture = mediaBrowser.getLibraryRoot(null)
            when (shuffleMode) {
                ShuffleMode.ON -> {
                    if (!mediaBrowser.shuffleModeEnabled)
                        mediaBrowser.sendCustomCommand(MusicService.COMMAND_SHUFFLE_MODE_ON, Bundle.EMPTY)
                }
                ShuffleMode.OFF -> {
                    if (mediaBrowser.shuffleModeEnabled)
                        mediaBrowser.sendCustomCommand(MusicService.COMMAND_SHUFFLE_MODE_OFF, Bundle.EMPTY)
                }
            }

            when (repeatMode) {
                RepeatMode.ONE -> mediaBrowser.sendCustomCommand(MusicService.COMMAND_REPEAT_MODE_ONE, Bundle.EMPTY)
                RepeatMode.ALL -> mediaBrowser.sendCustomCommand(MusicService.COMMAND_REPEAT_MODE_ALL, Bundle.EMPTY)
                RepeatMode.OFF -> mediaBrowser.sendCustomCommand(MusicService.COMMAND_REPEAT_MODE_OFF, Bundle.EMPTY)
            }

            rootFuture.addListener(
                {
                    val rootItem = rootFuture.get().value!!
                    fetchRootChildren(mediaBrowser, rootItem)
                },
                MoreExecutors.directExecutor()
            )
        }.launchIn(viewModelScope)
    }

    private fun fetchRootChildren(mediaBrowser: MediaBrowser, rootItem: MediaItem) {
        val childrenFuture = mediaBrowser.getChildren(rootItem.mediaId, 0, Int.MAX_VALUE, null)
        childrenFuture.addListener(
            {
                val result = childrenFuture.get()
                val children = result.value!!
                _uiState.update {
                    it.copy(rootChildren = children)
                }
            },
            MoreExecutors.directExecutor()
        )
    }

    private fun getShuffleMode() {
        viewModelScope.launch {

        }
    }

    override fun onCleared() {
        super.onCleared()
        musicServiceConnection.releaseBrowser()
    }

}

data class MainUiState(
    val rootChildren: List<MediaItem> = emptyList()
)