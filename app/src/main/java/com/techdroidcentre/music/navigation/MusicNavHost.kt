package com.techdroidcentre.music.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.media3.common.MediaItem
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.techdroidcentre.albumdetails.navigation.albumDetailsScreen
import com.techdroidcentre.albumdetails.navigation.navigateToAlbumDetails
import com.techdroidcentre.albums.navigation.albumsScreen
import com.techdroidcentre.artistdetails.navigation.artistDetailsScreen
import com.techdroidcentre.artistdetails.navigation.navigateToArtistDetails
import com.techdroidcentre.artists.navigation.artistsScreen
import com.techdroidcentre.home.navigation.homeNavigationRoute
import com.techdroidcentre.home.navigation.homeScreen
import com.techdroidcentre.player.ALBUMS_ID
import com.techdroidcentre.player.ARTISTS_ID
import com.techdroidcentre.player.SONGS_ID
import com.techdroidcentre.songs.navigation.songsScreen

@Composable
fun MusicNavHost(
    rootChildren: List<MediaItem>,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = homeNavigationRoute,
        modifier = modifier
    ) {
        homeScreen()
        songsScreen(songsId = rootChildren.first { it.mediaId == SONGS_ID }.mediaId)
        albumsScreen(
            albumsId = rootChildren.first { it.mediaId == ALBUMS_ID }.mediaId,
            navigateToAlbumDetails = { albumId ->
                navController.navigateToAlbumDetails(albumId)
            }
        )
        albumDetailsScreen(onBackPress = { navController.popBackStack() })
        artistsScreen(
            artistsId = rootChildren.first { it.mediaId == ARTISTS_ID }.mediaId,
            navigateToArtistDetails = { artistId ->
                navController.navigateToArtistDetails(artistId = artistId)
            }
        )
        artistDetailsScreen(
            navigateToAlbumDetails = { albumId ->
                navController.navigateToAlbumDetails(albumId)
            },
            onBackPress = { navController.popBackStack() }
        )
    }
}