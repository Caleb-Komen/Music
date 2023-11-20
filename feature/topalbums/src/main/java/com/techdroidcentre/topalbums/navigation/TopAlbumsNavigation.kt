package com.techdroidcentre.topalbums.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.techdroidcentre.topalbums.TopAlbumsScreen

const val topAlbumsNavigationRoute = "top_albums"

fun NavController.navigateToTopAlbums() {
    navigate(topAlbumsNavigationRoute)
}

fun NavGraphBuilder.topAlbumsScreen(navigateToAlbumDetails: (String) -> Unit, onBack: () -> Unit) {
    composable(topAlbumsNavigationRoute) {
        TopAlbumsScreen(navigateToAlbumDetails = navigateToAlbumDetails, onBack = onBack)
    }
}
