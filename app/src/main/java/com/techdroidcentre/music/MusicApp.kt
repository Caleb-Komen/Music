package com.techdroidcentre.music

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.media3.common.MediaItem
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.techdroidcentre.albums.navigation.albumsNavigationRoute
import com.techdroidcentre.artists.navigation.artistsNavigationRoute
import com.techdroidcentre.music.navigation.MusicNavHost
import com.techdroidcentre.music.navigation.TopLevelDestination
import com.techdroidcentre.music.navigation.TopLevelDestination.Albums
import com.techdroidcentre.music.navigation.TopLevelDestination.Artists
import com.techdroidcentre.music.navigation.TopLevelDestination.Songs
import com.techdroidcentre.nowplaying.NowPlayingSheet
import com.techdroidcentre.songs.navigation.songsNavigationRoute
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicApp(
    rootChildren: List<MediaItem>,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val scaffoldState = rememberBottomSheetScaffoldState()
    val scope = rememberCoroutineScope()
    var nowPlayingSheetCollapsed by remember { mutableStateOf(true) }
    val destinations = listOf(Songs, Albums, Artists)
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val bottomBarRoutes = listOf(songsNavigationRoute, albumsNavigationRoute, artistsNavigationRoute)
    val shouldShowBottomBar = navBackStackEntry?.destination?.route in bottomBarRoutes

    Scaffold(
        bottomBar = {
            AnimatedVisibility (shouldShowBottomBar && nowPlayingSheetCollapsed) {
                MusicBottomBar(
                    destinations = destinations,
                    currentDestination = currentDestination,
                    onNavigateToDestination = {
                        navController.navigate(it.route) {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    }
                )
            }
        },
        modifier = modifier
    ) {
        NowPlayingSheet(
            scaffoldState = scaffoldState,
            onSheetCollapsed = { isCollapsed -> nowPlayingSheetCollapsed = isCollapsed },
            modifier = Modifier.padding(it)
        ) { paddingValues ->
            MusicNavHost(
                rootChildren = rootChildren,
                navController = navController,
                modifier = Modifier.padding(paddingValues)
            )
        }
    }

    BackHandler(scaffoldState.bottomSheetState.currentValue == SheetValue.Expanded) {
        scope.launch {
            scaffoldState.bottomSheetState.partialExpand()
        }.invokeOnCompletion {
            nowPlayingSheetCollapsed = true
        }
    }
}

@Composable
fun MusicBottomBar(
    destinations: List<TopLevelDestination>,
    currentDestination: NavDestination?,
    onNavigateToDestination: (TopLevelDestination) -> Unit
) {
    NavigationBar {
        destinations.forEach { destination ->
            val selected = currentDestination?.hierarchy?.any {
                it.route == destination.route
            } ?: false
            NavigationBarItem(
                selected = selected,
                onClick = { onNavigateToDestination(destination) },
                icon = {
                    if (selected) {
                        Icon(
                            painter = painterResource(destination.selectedIcon),
                            contentDescription = null
                        )
                    } else {
                        Icon(
                            painter = painterResource(destination.unselectedIcon),
                            contentDescription = null
                        )
                    }
                },
                label = { Text(text = destination.iconText) }
            )
        }
    }
}