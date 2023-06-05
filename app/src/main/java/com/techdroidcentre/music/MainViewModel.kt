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
                    _uiState.update {
                        it.copy(rootMediaItem = rootItem)
                    }
                },
                MoreExecutors.directExecutor()
            )
        }.launchIn(viewModelScope)
    }

    fun setPermissionGranted(isGranted: Boolean) {
        _uiState.update {
            it.copy(isPermissionGranted = isGranted)
        }
    }

    override fun onCleared() {
        super.onCleared()
        musicServiceConnection.releaseBrowser()
    }

}

data class MainUiState(
    val isPermissionGranted: Boolean = false,
    val rootMediaItem: MediaItem = MediaItem.EMPTY
)