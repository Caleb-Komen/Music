package com.techdroidcentre.music.navigation

import com.techdroidcentre.albums.navigation.albumsNavigationRoute
import com.techdroidcentre.artists.navigation.artistsNavigationRoute
import com.techdroidcentre.home.navigation.homeNavigationRoute
import com.techdroidcentre.music.R
import com.techdroidcentre.songs.navigation.songsNavigationRoute

sealed class TopLevelDestination(
    val selectedIcon: Int,
    val unselectedIcon: Int,
    val iconText: String,
    val route: String,
) {
    object Home: TopLevelDestination(
        selectedIcon = R.drawable.home,
        unselectedIcon = R.drawable.home_outline,
        iconText = "Home",
        route = homeNavigationRoute
    )

    object Songs: TopLevelDestination(
        selectedIcon = R.drawable.music_note,
        unselectedIcon = R.drawable.music_note_outline,
        iconText = "Songs",
        route = songsNavigationRoute
    )

    object Albums: TopLevelDestination(
        selectedIcon = R.drawable.music_album,
        unselectedIcon = R.drawable.music_album_outline,
        iconText = "Albums",
        route = albumsNavigationRoute
    )

    object Artists: TopLevelDestination(
        selectedIcon = R.drawable.account_music,
        unselectedIcon = R.drawable.account_music_outline,
        iconText = "Artists",
        route = artistsNavigationRoute
    )
}