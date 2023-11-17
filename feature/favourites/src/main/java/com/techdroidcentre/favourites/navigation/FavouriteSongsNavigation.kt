package com.techdroidcentre.favourites.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.techdroidcentre.favourites.FavouriteSongsScreen

const val favouriteSongsNavigationRoute = "favourite_songs"

fun NavController.navigateToFavouriteSongs() {
    navigate(favouriteSongsNavigationRoute)
}

fun NavGraphBuilder.favouriteSongsScreen(onBackPress: () -> Unit) {
    composable(route = favouriteSongsNavigationRoute) {
        FavouriteSongsScreen(onBackPress = onBackPress)
    }
}