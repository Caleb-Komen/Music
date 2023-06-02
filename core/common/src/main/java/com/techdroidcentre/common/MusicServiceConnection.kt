package com.techdroidcentre.common

import android.content.ComponentName
import android.content.Context
import androidx.media3.session.MediaBrowser
import androidx.media3.session.SessionToken
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

    init {
        browserFuture.addListener(
            {
                val browser = browserFuture.get()
                mediaBrowser.update {
                    browser
                }
            },
            MoreExecutors.directExecutor()
        )
    }

    fun releaseBrowser() {
        MediaBrowser.releaseFuture(browserFuture)
    }
}