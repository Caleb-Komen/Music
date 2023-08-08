package com.techdroidcentre.artistdetails

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.session.R
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.techdroidcentre.model.Album

@Composable
fun ArtistDetailsScreen(
    navigateToAlbumDetails: (String) -> Unit,
    onBackPress: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ArtistDetailsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    ArtistDetailsScreen(
        uiState = uiState,
        modifier = modifier,
        navigateToAlbumDetails = navigateToAlbumDetails,
        onBackPress = onBackPress
    )
}

@Composable
fun ArtistDetailsScreen(
    uiState: ArtistDetailsUiState,
    navigateToAlbumDetails: (String) -> Unit,
    onBackPress: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding(),
        contentAlignment = Alignment.Center
    ){
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackPress) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Navigate back"
                    )
                }
                Text(
                    text = uiState.artist,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            LazyVerticalGrid(
                columns = GridCells.Adaptive(128.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                contentPadding = PaddingValues(horizontal = 24.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(items = uiState.albums, key = { it.id }) { album ->
                    AlbumItem(album = album, navigateToAlbumDetails = navigateToAlbumDetails)
                }
            }
        }
        if (!uiState.loading && uiState.albums.isEmpty()) EmptyAlbumsScreen(modifier = Modifier.padding(24.dp))

        if (uiState.error.isNotBlank()) {
            Text(text = uiState.error, modifier = Modifier.padding(24.dp))
        }

        if (uiState.loading) {
            CircularProgressIndicator()
        }
    }
}

@Composable
fun EmptyAlbumsScreen(modifier: Modifier = Modifier) {
    Text(
        text = "No albums found for artist.",
        style = MaterialTheme.typography.titleMedium,
        modifier = modifier
    )
}

@Composable
fun AlbumItem(
    album: Album,
    navigateToAlbumDetails: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .background(MaterialTheme.colorScheme.background)
            .clip(shape = MaterialTheme.shapes.small)
            .clickable { navigateToAlbumDetails(album.id.toString()) }
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(album.artworkUri)
                .crossfade(true)
                .build(),
            error = painterResource(R.drawable.media3_icon_circular_play),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(shape = MaterialTheme.shapes.small),
            contentScale = ContentScale.Crop
        )
        Text(
            text = album.name,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth()
        )
    }
}