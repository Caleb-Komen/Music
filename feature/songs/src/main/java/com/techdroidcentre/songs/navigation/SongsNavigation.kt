package com.techdroidcentre.songs.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.techdroidcentre.songs.SongsScreen

const val songsNavigationRoute = "songs"

fun NavGraphBuilder.songsScreen(songsId: String) {
    composable(songsNavigationRoute) {
        SongsScreen(songsId = songsId)
    }
}