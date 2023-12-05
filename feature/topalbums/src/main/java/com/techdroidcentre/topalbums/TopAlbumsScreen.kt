package com.techdroidcentre.topalbums

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.techdroidcentre.designsystem.component.AlbumItem
import com.techdroidcentre.designsystem.ui.DefaultScreen
import com.techdroidcentre.model.Album

@Composable
fun TopAlbumsScreen(
    navigateToAlbumDetails: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TopAlbumsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    DefaultScreen(
        error = uiState.error,
        loading = uiState.loading,
        modifier = modifier
    ) {
        TopAlbumsContent(
            albums = uiState.topAlbums,
            navigateToAlbumDetails = navigateToAlbumDetails,
            onBack = onBack
        )
    }
}

@Composable
fun TopAlbumsContent(
    albums: List<Album>,
    navigateToAlbumDetails: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        TopBar(onBack = onBack)
        LazyVerticalGrid(
            columns = GridCells.Adaptive(128.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            contentPadding = PaddingValues(horizontal = 24.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(items = albums, key = { it.id }) { album ->
                AlbumItem(album = album, navigateToAlbumDetails = navigateToAlbumDetails)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "Top Albums",
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Navigate back")
            }
        },
        modifier = modifier
    )
}

