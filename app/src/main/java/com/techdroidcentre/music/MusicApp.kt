package com.techdroidcentre.music

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.media3.common.MediaItem
import com.techdroidcentre.nowplaying.NowPlayingSheet
import com.techdroidcentre.player.SONGS_ID
import com.techdroidcentre.songs.SongsScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicApp(
    rootChildren: List<MediaItem>,
    modifier: Modifier = Modifier
) {
    var nowPlayingSheetCollapsed by remember { mutableStateOf(true) }
    val mediaItem = rootChildren.first { it.mediaId == SONGS_ID }
    NowPlayingSheet(
        onSheetCollapsed = { isCollapsed -> nowPlayingSheetCollapsed = isCollapsed },
        modifier = modifier
    ) { paddingValues ->
        SongsScreen(
            songsId = mediaItem.mediaId,
            modifier = Modifier.padding(paddingValues)
        )
    }
}