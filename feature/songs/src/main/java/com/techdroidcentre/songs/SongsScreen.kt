package com.techdroidcentre.songs

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
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.techdroidcentre.designsystem.component.AudioWave
import com.techdroidcentre.designsystem.icon.MusicIcons
import com.techdroidcentre.model.Song

@Composable
fun SongsScreen(
    songsId: String,
    modifier: Modifier = Modifier,
    viewModel: SongsViewModel = viewModel(factory = SongsViewModel.provideFactory(songsId))
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
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        Column {
            Text(
                text = "Songs",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 24.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                items(items = uiState.songs, key = { it.id }) { song ->
                    SongItem(
                        song = song,
                        playingSongId = uiState.playingSongId,
                        isSongPlaying = uiState.isSongPlaying,
                        playOrPause = playOrPause
                    )
                }
            }
        }
        if (!uiState.loading && uiState.songs.isEmpty()) EmptySongsScreen(modifier = Modifier.padding(24.dp))

        if (uiState.error.isNotBlank()) {
            Text(text = uiState.error, modifier = Modifier.padding(24.dp))
        }

        if (uiState.loading) {
            CircularProgressIndicator()
        }
    }
}

@Composable
fun EmptySongsScreen(modifier: Modifier = Modifier) {
    Text(
        text = "No music found.",
        style = MaterialTheme.typography.titleMedium,
        modifier = modifier
    )
}

@Composable
fun SongItem(
    song: Song,
    playingSongId: String,
    isSongPlaying: Boolean,
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
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.padding(start = 24.dp)
            ){
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
                if (song.id.toString() == playingSongId) {
                    AudioWave(isSongPlaying = isSongPlaying)
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.padding(end = 24.dp)) {
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
        Divider(thickness = 0.5.dp, modifier = Modifier.padding(horizontal = 24.dp))
    }
}

