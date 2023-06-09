package com.techdroidcentre.music

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.session.MediaBrowser
import com.google.common.util.concurrent.MoreExecutors
import com.techdroidcentre.common.MusicServiceConnection
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val musicServiceConnection: MusicServiceConnection
): ViewModel() {
    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState

    init {
        musicServiceConnection.mediaBrowser.onEach { browser ->
            val mediaBrowser: MediaBrowser = browser ?: return@onEach
            val rootFuture = mediaBrowser.getLibraryRoot(null)

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

    override fun onCleared() {
        super.onCleared()
        musicServiceConnection.releaseBrowser()
    }

}

data class MainUiState(
    val rootChildren: List<MediaItem> = emptyList()
)