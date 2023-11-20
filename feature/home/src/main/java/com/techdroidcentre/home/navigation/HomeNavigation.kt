package com.techdroidcentre.home.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.techdroidcentre.home.HomeScreen

const val homeNavigationRoute = "home"

fun NavGraphBuilder.homeScreen(
    navigateToPlaylistsScreen: () -> Unit,
    navigateToAlbumDetail: (String) -> Unit,
    navigateToFavouriteSongs: () -> Unit,
    navigateToTopAlbums: () -> Unit
) {
    composable(homeNavigationRoute) {
        HomeScreen(
            navigateToPlaylistsScreen = navigateToPlaylistsScreen,
            navigateToAlbumDetail = navigateToAlbumDetail,
            navigateToFavouriteSongs = navigateToFavouriteSongs,
            navigateToTopAlbums = navigateToTopAlbums
        )
    }
}