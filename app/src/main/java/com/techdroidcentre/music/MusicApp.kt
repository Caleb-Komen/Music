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
import com.techdroidcentre.songs.SongsScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicApp(
    rootMediaItem: MediaItem,
    modifier: Modifier = Modifier
) {
    var nowPlayingSheetCollapsed by remember { mutableStateOf(true) }
    NowPlayingSheet(
        onSheetCollapsed = { isCollapsed -> nowPlayingSheetCollapsed = isCollapsed },
        modifier = modifier
    ) { paddingValues ->
        SongsScreen(
            rootMediaItem = rootMediaItem,
            modifier = Modifier.padding(paddingValues)
        )
    }
}