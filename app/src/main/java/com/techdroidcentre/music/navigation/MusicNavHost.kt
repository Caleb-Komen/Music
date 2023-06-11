package com.techdroidcentre.music.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.media3.common.MediaItem
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.techdroidcentre.albums.navigation.albumsScreen
import com.techdroidcentre.player.ALBUMS_ID
import com.techdroidcentre.player.SONGS_ID
import com.techdroidcentre.songs.navigation.songsNavigationRoute
import com.techdroidcentre.songs.navigation.songsScreen

@Composable
fun MusicNavHost(
    rootChildren: List<MediaItem>,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = songsNavigationRoute,
        modifier = modifier
    ) {
        songsScreen(songsId = rootChildren.first { it.mediaId == SONGS_ID }.mediaId)
        albumsScreen(
            albumsId = rootChildren.first { it.mediaId == ALBUMS_ID }.mediaId,
            navigateToAlbumDetail = {}
        )
    }
}