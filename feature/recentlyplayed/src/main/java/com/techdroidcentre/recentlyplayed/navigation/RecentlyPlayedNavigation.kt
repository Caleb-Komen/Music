package com.techdroidcentre.recentlyplayed.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.techdroidcentre.recentlyplayed.RecentlyPlayedSongsScreen

const val recentlyPlayedSongsNavigationRoute = "recently_played_songs"

fun NavController.navigateToRecentlyPlayedSongs() {
    navigate(recentlyPlayedSongsNavigationRoute)
}

fun NavGraphBuilder.recentlyPlayedSongsScreen(onBack: () -> Unit) {
    composable(recentlyPlayedSongsNavigationRoute) {
        RecentlyPlayedSongsScreen(onBack = onBack)
    }
}