package com.techdroidcentre.common

import android.content.ComponentName
import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.session.MediaBrowser
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.collect.ImmutableList
import com.google.common.util.concurrent.MoreExecutors
import com.techdroidcentre.player.MusicService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class MusicServiceConnection @Inject constructor(
    @ApplicationContext context: Context
) {
    private val sessionToken = SessionToken(context, ComponentName(context, MusicService::class.java))

    private val browserFuture = MediaBrowser.Builder(context, sessionToken).buildAsync()

    private val controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()

    val mediaBrowser: MutableStateFlow<MediaBrowser?> = MutableStateFlow(null)

    val mediaController: MutableStateFlow<MediaController?> = MutableStateFlow(null)

    val nowPlaying: MutableStateFlow<MediaItem> = MutableStateFlow(MediaItem.EMPTY)

    val isPlaying: MutableStateFlow<Boolean> = MutableStateFlow(false)

    init {
        browserFuture.addListener(
            {
                val browser = browserFuture.get()
                mediaBrowser.update {
                    browser
                }
                browser.addListener(playerListener)
            },
            MoreExecutors.directExecutor()
        )
        controllerFuture.addListener(
            {
                val controller = controllerFuture.get()
                mediaController.update {
                    controller
                }
            },
            MoreExecutors.directExecutor()
        )
    }

    fun getChildren(parentId: String): List<MediaItem> {
        val browser = mediaBrowser.value ?: return emptyList()
        val items = mutableListOf<MediaItem>()
        val childrenFuture = browser.getChildren(parentId, 0, 100, null)
        childrenFuture.addListener(
            {
                val children = childrenFuture.get().value ?: emptyList()
                items.addAll(children)
            },
            MoreExecutors.directExecutor()
        )
        return ImmutableList.copyOf(items)
    }

    private val playerListener = object: Player.Listener {
        override fun onEvents(player: Player, events: Player.Events) {
            if (events.contains(Player.EVENT_MEDIA_ITEM_TRANSITION)) {
                mediaBrowser.value?.currentMediaItem?.let { mediaItem ->
                    nowPlaying.update {
                        mediaItem
                    }
                }
            }

            if (events.contains(Player.EVENT_IS_PLAYING_CHANGED)) {
                isPlaying.update {
                    player.isPlaying
                }
            }
        }
    }

    fun releaseBrowser() {
        mediaBrowser.value?.removeListener(playerListener)
        MediaBrowser.releaseFuture(browserFuture)
    }

    fun releaseController() {
        MediaController.releaseFuture(controllerFuture)
    }
}