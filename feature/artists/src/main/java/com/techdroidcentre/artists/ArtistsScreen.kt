package com.techdroidcentre.artists

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.techdroidcentre.designsystem.ui.DefaultScreen
import com.techdroidcentre.model.Artist

@Composable
fun ArtistsScreen(
    artistsId: String,
    navigateToArtistDetails: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ArtistsViewModel = viewModel(factory = ArtistsViewModel.provideFactory(artistsId))
) {
    val uiState by viewModel.uiState.collectAsState()
    ArtistsScreen(
        uiState = uiState,
        navigateToArtistDetails = navigateToArtistDetails,
        modifier = modifier
    )
}

@Composable
fun ArtistsScreen(
    uiState: ArtistsUiState,
    navigateToArtistDetails: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    DefaultScreen(
        error = uiState.error,
        loading = uiState.loading,
        modifier = modifier
    ) {
        Column {
            Text(
                text = "Artists",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 24.dp)
            )
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(items = uiState.artists, key = { it.id }) { artist ->
                    ArtistItem(artist = artist, navigateToArtistDetails = navigateToArtistDetails)
                }
            }
        }
        if (!uiState.loading && uiState.artists.isEmpty()) EmptyArtistsScreen(modifier = Modifier.padding(24.dp))
    }
}

@Composable
fun EmptyArtistsScreen(modifier: Modifier = Modifier) {
    Text(
        text = "No artists found.",
        style = MaterialTheme.typography.titleMedium,
        modifier = modifier
    )
}

@Composable
fun ArtistItem(
    artist: Artist,
    navigateToArtistDetails: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.clickable { navigateToArtistDetails(artist.id) }) {
        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_user_place_holder),
                    contentDescription = null,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = artist.name)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Divider(thickness = 0.5.dp)
        }
    }
}