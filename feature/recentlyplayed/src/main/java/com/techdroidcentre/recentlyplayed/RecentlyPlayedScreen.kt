package com.techdroidcentre.recentlyplayed

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.techdroidcentre.designsystem.icon.MusicIcons
import com.techdroidcentre.model.Song

@Composable
fun RecentlyPlayedSongsScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RecentlyPlayedViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        RecentlyPlayedSongs(
            songs = uiState.recentlyPlayed,
            play = viewModel::play,
            shuffle = viewModel::shuffle,
            playOrPause = viewModel::playOrPause,
            onBack = onBack
        )
        if (uiState.error.isNotBlank()) {
            Text(text = uiState.error, modifier = Modifier.padding(24.dp))
        }

        if (uiState.loading) {
            CircularProgressIndicator()
        }
    }
}

@Composable
fun RecentlyPlayedSongs(
    songs: List<Song>,
    play: () -> Unit,
    shuffle: () -> Unit,
    playOrPause: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        TopBar(onBack = onBack)
        MediaControls(
            play = play,
            shuffle = shuffle,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            item { Spacer(modifier = Modifier.height(24.dp)) }
            items(items = songs, key = { it.id }) { song ->
                SongItem(
                    song = song,
                    playOrPause = playOrPause
                )
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
                text = "Recently Played",
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

@Composable
fun MediaControls(
    play: () -> Unit,
    shuffle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Button(
            onClick = play,
            modifier = Modifier.weight(1f),
            shape = CircleShape
        ) {
            Text(
                text = "Play"
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Button(
            onClick = shuffle,
            modifier = Modifier.weight(1f),
            shape = CircleShape
        ) {
            Text(
                text = "Shuffle"
            )
        }
    }
}

@Composable
fun SongItem(
    song: Song,
    playOrPause: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.clickable { playOrPause(song.id.toString()) }
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(song.artworkData)
                    .crossfade(true)
                    .build(),
                error = painterResource(MusicIcons.defaultMusicNote),
                contentDescription = null,
                modifier = Modifier
                    .padding(start = 24.dp)
                    .size(56.dp)
                    .clip(MaterialTheme.shapes.medium),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier
                .padding(end = 24.dp)
                .weight(1f)) {
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