package com.techdroidcentre.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.techdroidcentre.designsystem.icon.MusicIcons
import com.techdroidcentre.designsystem.theme.MusicTheme
import com.techdroidcentre.model.Album
import com.techdroidcentre.model.Song

@Composable
fun HomeScreen(
    navigateToPlaylistsScreen: () -> Unit,
    navigateToAlbumDetail: (String) -> Unit,
    navigateToFavouriteSongs: () -> Unit,
    navigateToTopAlbums: () -> Unit,
    navigateToRecentlyPlayed: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    Column(modifier = modifier
        .fillMaxSize()
        .statusBarsPadding()) {
        Text(
            text = "Home",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(24.dp)
                ) {
                    PlaylistsCard(
                        navigateToPlaylistsScreen = navigateToPlaylistsScreen,
                        modifier = Modifier.weight(1f)
                    )
                    FavouritesCard(
                        navigateToFavouriteSongs = navigateToFavouriteSongs,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            item {
                AnimatedVisibility(
                    visible = uiState.topAlbums.isNotEmpty(),
                    enter = slideInVertically(),
                    exit = slideOutVertically()
                ) {
                    TopAlbumsRow(
                        albums = uiState.topAlbums,
                        navigateToAlbumDetail = navigateToAlbumDetail,
                        navigateToTopAlbums = navigateToTopAlbums
                    )
                }
            }
            recentlyPlayedSongs(
                songs = uiState.recentlyPlayed,
                playOrPause = viewModel::playOrPause,
                navigateToRecentlyPlayed = navigateToRecentlyPlayed
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistsCard(
    navigateToPlaylistsScreen: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = navigateToPlaylistsScreen,
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
                .fillMaxSize()
        ) {
            Image(
                painter = painterResource(MusicIcons.queueMusic),
                contentDescription = null,
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
                modifier = Modifier.size(40.dp)
            )
            Text(
                text = "Playlists",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavouritesCard(
    navigateToFavouriteSongs: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = navigateToFavouriteSongs,
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
                .fillMaxSize()
        ) {
            Image(
                imageVector = Icons.Default.Favorite,
                contentDescription = null,
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
                modifier = Modifier.size(40.dp)
            )
            Text(
                text = "Favourites",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
fun TopAlbumsRow(
    albums: List<Album>,
    navigateToAlbumDetail: (String) -> Unit,
    navigateToTopAlbums: () -> Unit,
    modifier: Modifier = Modifier
) {
    val lastIndex = albums.size - 1
    Column(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            Text(
                text = "Top Albums",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "More >",
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(start = 8.dp)
                    .clickable(onClick = navigateToTopAlbums)
            )
        }
        LazyRow(
            contentPadding = PaddingValues(start = 24.dp, top = 8.dp, end = 24.dp, bottom = 24.dp)
        ) {
            itemsIndexed(items = albums, key = { _, album -> album.id }) { index, album ->
                TopAlbumItem(
                    album = album,
                    navigateToAlbumDetail = navigateToAlbumDetail
                )
                if (index < lastIndex) Spacer(Modifier.width(24.dp))
            }
        }
    }
}

fun LazyListScope.recentlyPlayedSongs(
    songs: List<Song>,
    playOrPause: (String) -> Unit,
    navigateToRecentlyPlayed: () -> Unit
) {
    item {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            Text(
                text = "Recently Played Songs",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "More >",
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(start = 8.dp)
                    .clickable(onClick = navigateToRecentlyPlayed)
            )
        }
    }
    items(items = songs, key = { it.id }) { song ->
        RecentlyPlayedSongItem(song = song, playOrPause = playOrPause)
    }
}

@Composable
fun TopAlbumItem(
    album: Album,
    navigateToAlbumDetail: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(128.dp)
            .clickable { navigateToAlbumDetail(album.id.toString()) }
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(album.artworkUri)
                .crossfade(true)
                .build(),
            error = painterResource(MusicIcons.musicRecord),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(MaterialTheme.shapes.small),
            contentScale = ContentScale.Crop
        )
        Text(
            text = album.name,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth()
        )
    }
}

@Composable
fun RecentlyPlayedSongItem(
    song: Song,
    playOrPause: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(vertical = 4.dp)
            .clickable { playOrPause(song.id.toString()) }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 24.dp)
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(song.artworkData)
                    .crossfade(true)
                    .build(),
                error = painterResource(MusicIcons.defaultMusicNote),
                contentDescription = null,
                modifier = Modifier
                    .size(56.dp)
                    .clip(MaterialTheme.shapes.medium),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = song.title,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${song.album} - ${song.artist}",
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Divider(thickness = 0.5.dp)
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    MusicTheme {
        HomeScreen({}, {}, {}, {}, {})
    }
}