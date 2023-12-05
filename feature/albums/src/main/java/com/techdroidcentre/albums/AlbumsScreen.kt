package com.techdroidcentre.albums

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.techdroidcentre.designsystem.component.AlbumItem
import com.techdroidcentre.designsystem.ui.DefaultScreen

@Composable
fun AlbumsScreen(
    albumsId: String,
    navigateToAlbumDetails: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AlbumsViewModel = viewModel(factory = AlbumsViewModel.provideFactory(albumsId))
) {
    val uiState by viewModel.uiState.collectAsState()
    AlbumsScreen(
        uiState = uiState,
        navigateToAlbumDetails = navigateToAlbumDetails,
        modifier = modifier
    )
}

@Composable
fun AlbumsScreen(
    uiState: AlbumsUiState,
    navigateToAlbumDetails: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    DefaultScreen(
        error = uiState.error,
        loading = uiState.loading,
        modifier = modifier
    ) {
        Column {
            Text(
                text = "Albums",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 24.dp)
            )
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

    }
}

@Composable
fun EmptyAlbumsScreen(modifier: Modifier = Modifier) {
    Text(
        text = "No albums found.",
        style = MaterialTheme.typography.titleMedium,
        modifier = modifier
    )
}
