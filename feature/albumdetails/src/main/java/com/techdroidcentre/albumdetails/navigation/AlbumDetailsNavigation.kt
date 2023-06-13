package com.techdroidcentre.albumdetails.navigation

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.techdroidcentre.albumdetails.AlbumDetailsScreen

internal const val albumIdArg = "albumId"
const val albumDetailsNavigationRoute = "album_details"

internal class AlbumDetailsArgs(val albumId: String?) {
    constructor(savedStateHandle: SavedStateHandle): this(savedStateHandle[albumIdArg])
}

fun NavController.navigateToAlbumDetails(albumId: String) {
    navigate("$albumDetailsNavigationRoute/$albumId")
}

fun NavGraphBuilder.albumDetailsScreen() {
    composable(
        route = "$albumDetailsNavigationRoute/{$albumIdArg}",
        arguments = listOf(
            navArgument(albumIdArg) { type = NavType.StringType }
        )
    ) {
        AlbumDetailsScreen()
    }
}