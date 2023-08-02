package com.techdroidcentre.home.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.techdroidcentre.home.HomeScreen

const val homeNavigationRoute = "home"

fun NavGraphBuilder.homeScreen(navigateToPlaylistsScreen: () -> Unit) {
    composable(homeNavigationRoute) {
        HomeScreen(navigateToPlaylistsScreen = navigateToPlaylistsScreen)
    }
}