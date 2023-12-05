package com.techdroidcentre.artistdetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.techdroidcentre.artistdetails.components.SortAlbumsDropdownMenu
import com.techdroidcentre.data.datastore.ArtistAlbumsSortOption
import com.techdroidcentre.designsystem.component.AlbumItem
import com.techdroidcentre.designsystem.icon.MusicIcons
import com.techdroidcentre.designsystem.ui.DefaultScreen

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
        onBackPress = onBackPress,
        onSortAlbums = viewModel::setArtistAlbumsSortOption
    )
}

@Composable
fun ArtistDetailsScreen(
    uiState: ArtistDetailsUiState,
    navigateToAlbumDetails: (String) -> Unit,
    onBackPress: () -> Unit,
    onSortAlbums: (ArtistAlbumsSortOption) -> Unit,
    modifier: Modifier = Modifier
) {
    DefaultScreen(
        error = uiState.error,
        loading = uiState.loading,
        modifier = modifier
    ){
        Column {
            ArtistAlbumsTopBar(
                artist = uiState.artist,
                sortOption = uiState.sortOption.name,
                onBackPress = onBackPress,
                onSortAlbums = onSortAlbums
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
fun ArtistAlbumsTopBar(
    artist: String,
    sortOption: String,
    onBackPress: () -> Unit,
    onSortAlbums: (ArtistAlbumsSortOption) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBackPress) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Navigate back"
            )
        }
        Text(
            text = artist,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f)
        )
        Box {
            IconButton(
                onClick = { expanded = true }
            ) {
                Icon(
                    painter = painterResource(MusicIcons.sort),
                    contentDescription = "Sort Albums"
                )
            }
            SortAlbumsDropdownMenu(
                expanded = expanded,
                sortOption = sortOption,
                onDismissRequest = { expanded = false },
                onSortAlbums = {
                    onSortAlbums(it)
                    expanded = false
                }
            )
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
