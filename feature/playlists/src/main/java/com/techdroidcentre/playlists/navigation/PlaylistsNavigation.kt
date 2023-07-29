package com.techdroidcentre.playlists.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.techdroidcentre.playlists.PlaylistsScreen

const val playlistsNavigationRoute = "playlists"

fun NavController.navigateToPlaylistsScreen() {
    navigate(playlistsNavigationRoute)
}

fun NavGraphBuilder.playlistsScreen(onBackPress: () -> Unit, navigateToPlaylistSongs: (Long) -> Unit) {
    composable(playlistsNavigationRoute) {
        PlaylistsScreen(onBackPress = onBackPress, navigateToPlaylistSongs = navigateToPlaylistSongs)
    }
}