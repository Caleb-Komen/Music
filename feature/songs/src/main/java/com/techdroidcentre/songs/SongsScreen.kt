package com.techdroidcentre.songs

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.util.Size
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.MediaItem
import coil.compose.AsyncImage
import coil.request.ImageRequest

@Composable
fun SongsScreen(
    rootMediaItem: MediaItem,
    modifier: Modifier = Modifier,
    viewModel: SongsViewModel = viewModel(factory = SongsViewModel.provideFactory(rootMediaItem))
) {
    val uiState by viewModel.uiState.collectAsState()
    SongsScreen(
        uiState = uiState,
        playOrPause = viewModel::playOrPause,
        modifier = modifier
    )
}

@Composable
fun SongsScreen(
    uiState: SongsUiState,
    playOrPause: (Song) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            item {
                Text(
                    text = "Songs",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            items(items = uiState.songs, key = { it.id }) { song ->
                SongItem(song = song, playOrPause = playOrPause)
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
fun SongItem(
    song: Song,
    playOrPause: (Song) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.clickable { playOrPause(song) }
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                            getThumbnail(LocalContext.current, song.artworkUri)
                        else song.artworkData
                    )
                    .crossfade(true)
                    .build(),
                error = painterResource(androidx.media3.session.R.drawable.media3_icon_circular_play),
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
                    text = song.artist,
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

@RequiresApi(Build.VERSION_CODES.Q)
fun getThumbnail(context: Context, artworkUri: String): Bitmap? {
    return try {
        context.contentResolver.loadThumbnail(
            Uri.parse(artworkUri),
            Size(300, 300),
            null
        )
    } catch(e: Exception) {
        null
    }
}