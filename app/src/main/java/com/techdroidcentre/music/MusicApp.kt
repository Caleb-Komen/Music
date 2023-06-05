package com.techdroidcentre.music

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.media3.common.MediaItem
import com.techdroidcentre.nowplaying.NowPlayingSheet
import com.techdroidcentre.player.SONGS_ID
import com.techdroidcentre.songs.SongsScreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicApp(
    rootChildren: List<MediaItem>,
    modifier: Modifier = Modifier
) {
    val scaffoldState = rememberBottomSheetScaffoldState()
    val scope = rememberCoroutineScope()
    var nowPlayingSheetCollapsed by remember { mutableStateOf(true) }
    val mediaItem = rootChildren.first { it.mediaId == SONGS_ID }
    NowPlayingSheet(
        scaffoldState = scaffoldState,
        onSheetCollapsed = { isCollapsed -> nowPlayingSheetCollapsed = isCollapsed },
        modifier = modifier
    ) { paddingValues ->
        SongsScreen(
            songsId = mediaItem.mediaId,
            modifier = Modifier.padding(paddingValues)
        )
    }

    BackHandler(scaffoldState.bottomSheetState.currentValue == SheetValue.Expanded) {
        scope.launch {
            scaffoldState.bottomSheetState.partialExpand()
        }.invokeOnCompletion {
            nowPlayingSheetCollapsed = true
        }
    }
}