package com.techdroidcentre.common

import android.content.ComponentName
import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.session.MediaBrowser
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

    val mediaBrowser: MutableStateFlow<MediaBrowser?> = MutableStateFlow(null)

    val nowPlaying: MutableStateFlow<MediaItem> = MutableStateFlow(MediaItem.EMPTY)

    val isPlaying: MutableStateFlow<Boolean> = MutableStateFlow(false)

    val duration: MutableStateFlow<Long> = MutableStateFlow(0L)

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

    fun getMediaItem(mediaId: String): MediaItem {
        val browser = mediaBrowser.value ?: return MediaItem.EMPTY
        var item = MediaItem.EMPTY
        val itemFuture = browser.getItem(mediaId)
        itemFuture.addListener(
            {
                item = itemFuture.get().value ?: MediaItem.EMPTY
            },
            MoreExecutors.directExecutor()
        )
        return item
    }

    private val playerListener = object: Player.Listener {
        override fun onEvents(player: Player, events: Player.Events) {
            if (events.contains(Player.EVENT_MEDIA_ITEM_TRANSITION)) {
                player.currentMediaItem?.let { mediaItem ->
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

            if (events.contains(Player.EVENT_MEDIA_ITEM_TRANSITION) ||
                events.contains(Player.EVENT_PLAY_WHEN_READY_CHANGED) ||
                events.contains(Player.EVENT_PLAYBACK_STATE_CHANGED)) {
                if (player.duration > 0) {
                    duration.update {
                        player.duration
                    }
                }
            }
        }
    }

    fun releaseBrowser() {
        mediaBrowser.value?.removeListener(playerListener)
        MediaBrowser.releaseFuture(browserFuture)
    }

}