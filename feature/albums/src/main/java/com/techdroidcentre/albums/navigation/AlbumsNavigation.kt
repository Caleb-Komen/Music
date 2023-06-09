package com.techdroidcentre.albums.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.techdroidcentre.albums.AlbumsScreen

const val albumsNavigationRoute = "albums"

fun NavGraphBuilder.albumsScreen(albumsId: String, navigateToAlbumDetails: (String) -> Unit) {
    composable(albumsNavigationRoute) {
        AlbumsScreen(albumsId = albumsId, navigateToAlbumDetails = navigateToAlbumDetails)
    }
}