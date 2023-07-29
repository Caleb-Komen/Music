package com.techdroidcentre.playlistsongs.navigation

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.techdroidcentre.playlistsongs.PlaylistSongsScreen

internal const val playlistIdArg = "playlistId"
internal const val songsRootArg = "songsRoot"
const val playlistSongsNavigationRoute = "playlist_songs"

internal class PlaylistSongsArgs(val playlistId: Long?, val songsRoot: String?) {
    constructor(savedStateHandle: SavedStateHandle): this(savedStateHandle[playlistIdArg], savedStateHandle[songsRootArg])
}

fun NavController.navigateToPlaylistSongs(playlistId: Long, songsRoot: String) {
    navigate("$playlistSongsNavigationRoute/$playlistId/$songsRoot")
}

fun NavGraphBuilder.playlistSongsScreen(onBackPress: () -> Unit) {
    composable(
        route = "$playlistSongsNavigationRoute/{$playlistIdArg}/{$songsRootArg}",
        arguments = listOf(
            navArgument(playlistIdArg) { type = NavType.LongType },
            navArgument(songsRootArg) { type = NavType.StringType }
        )
    ) {
        PlaylistSongsScreen(onBackPress = onBackPress)
    }
}