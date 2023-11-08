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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Card
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

@Composable
fun HomeScreen(
    navigateToPlaylistsScreen: () -> Unit,
    navigateToAlbumDetail: (String) -> Unit,
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
                PlaylistsCard(
                    navigateToPlaylistsScreen = navigateToPlaylistsScreen,
                    modifier = Modifier.padding(24.dp)
                )
            }
            item {
                AnimatedVisibility(
                    visible = uiState.topAlbums.isNotEmpty(),
                    enter = slideInVertically(),
                    exit = slideOutVertically()
                ) {
                    TopAlbumsRow(
                        albums = uiState.topAlbums,
                        navigateToAlbumDetail = navigateToAlbumDetail
                    )
                }
            }
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
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Image(
                painter = painterResource(MusicIcons.queueMusic),
                contentDescription = null,
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Playlists",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
fun TopAlbumsRow(
    albums: List<Album>,
    navigateToAlbumDetail: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val lastIndex = albums.size - 1
    Column(modifier = modifier) {
        Text(
            text = "Top Albums",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
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

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    MusicTheme {
        HomeScreen({}, {})
    }
}