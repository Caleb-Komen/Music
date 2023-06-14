package com.techdroidcentre.artists

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import com.techdroidcentre.model.Artist

@Composable
fun ArtistsScreen(
    artistsId: String,
    modifier: Modifier = Modifier,
    viewModel: ArtistsViewModel = viewModel(factory = ArtistsViewModel.provideFactory(artistsId))
) {
    val uiState by viewModel.uiState.collectAsState()
    ArtistsScreen(uiState = uiState, modifier = modifier)
}

@Composable
fun ArtistsScreen(
    uiState: ArtistsUiState,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column {
            Text(
                text = "Artists",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 24.dp)
            )
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 24.dp)
            ) {
                items(items = uiState.artists, key = { it.id }) { artist ->
                    ArtistItem(artist = artist)
                }
            }
        }
        if (uiState.error.isNotBlank()) {
            Text(text = uiState.error)
        }

        if (uiState.loading) {
            CircularProgressIndicator(
                modifier = Modifier
            )
        }
    }
}

@Composable
fun ArtistItem(
    artist: Artist,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
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