package com.techdroidcentre.artistdetails.navigation

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.techdroidcentre.artistdetails.ArtistDetailsScreen

const val artistDetailNavigationRoute = "artist_details"
const val artistIdArg = "artistId"

internal class ArtistDetailsArg(val artistId: String?) {
    constructor(savedStateHandle: SavedStateHandle): this(savedStateHandle[artistIdArg])
}

fun NavController.navigateToArtistDetails(artistId: String) {
    navigate("$artistDetailNavigationRoute/$artistId")
}

fun NavGraphBuilder.artistDetailsScreen(
    navigateToAlbumDetails: (String) -> Unit,
    onBackPress: () -> Unit
) {
    composable(
        route = "$artistDetailNavigationRoute/{$artistIdArg}",
        arguments = listOf(
            navArgument(artistIdArg){ type = NavType.StringType }
        )
    ) {
        ArtistDetailsScreen(
            navigateToAlbumDetails = navigateToAlbumDetails,
            onBackPress = onBackPress
        )
    }
}