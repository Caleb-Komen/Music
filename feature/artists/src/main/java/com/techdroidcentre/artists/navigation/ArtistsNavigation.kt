package com.techdroidcentre.artists.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.techdroidcentre.artists.ArtistsScreen

const val artistsNavigationRoute = "artists"

fun NavGraphBuilder.artistsScreen(artistsId: String, navigateToArtistDetails: (String) -> Unit,) {
    composable(artistsNavigationRoute) {
        ArtistsScreen(artistsId = artistsId, navigateToArtistDetails = navigateToArtistDetails)
    }
}