package com.techdroidcentre.albumdetails

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Divider
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
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.techdroidcentre.designsystem.component.MediaControls
import com.techdroidcentre.designsystem.icon.MusicIcons
import com.techdroidcentre.model.Song

@Composable
fun AlbumDetailsScreen(
    onBackPress: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AlbumDetailsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    AlbumDetailsScreen(
        uiState = uiState,
        playOrPause = viewModel::playOrPause,
        play = viewModel::play,
        shuffle = viewModel::shuffle,
        onBackPress = onBackPress,
        modifier = modifier
    )
}

@Composable
fun AlbumDetailsScreen(
    uiState: AlbumDetailsUiState,
    playOrPause: (String) -> Unit,
    play: () -> Unit,
    shuffle: () -> Unit,
    onBackPress: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.statusBarsPadding()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBackPress) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Navigate back"
                )
            }
            Text(
                text = uiState.album.name,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            item { AlbumInfo(uiState = uiState, modifier = Modifier.padding(horizontal = 24.dp)) }
            item { Spacer(modifier = Modifier.height(16.dp)) }
            if (!uiState.loading && uiState.songs.isNotEmpty()) {
                item {
                    MediaControls(
                        play = play,
                        shuffle = shuffle,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                }
                item { Spacer(modifier = Modifier.height(16.dp)) }
                albumSongsCollection(uiState = uiState, playOrPause = playOrPause)
            } else {
                item { EmptySongsScreen(modifier = Modifier.padding(24.dp)) }
            }
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
fun AlbumInfo(
    uiState: AlbumDetailsUiState,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(uiState.album.artworkUri)
                    .crossfade(true)
                    .build(),
                error = painterResource(MusicIcons.musicRecord),
                contentDescription = null,
                modifier = Modifier
                    .size(172.dp)
                    .clip(MaterialTheme.shapes.small),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = uiState.album.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = uiState.album.artist,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                if (uiState.album.year > 0) {
                    Text(
                        text = "Album • ${uiState.album.year}",
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Divider(thickness = 0.5.dp)
    }
}

fun LazyListScope.albumSongsCollection(
    uiState: AlbumDetailsUiState,
    playOrPause: (String) -> Unit,
) {
    val stringValue = if (uiState.songs.size > 1) "Songs" else "Song"
    item {
        Text(
            text = "${uiState.songs.size} $stringValue",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
    }
    item { Spacer(modifier = Modifier.height(4.dp)) }
    item { Divider(thickness = 0.5.dp, modifier = Modifier.padding(horizontal = 24.dp)) }
    items(items = uiState.songs) { song ->
        AlbumSongItem(song = song, playOrPause = playOrPause)
    }
}

@Composable
fun AlbumSongItem(
    song: Song,
    playOrPause: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.clickable { playOrPause(song.id.toString()) }) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 24.dp)
        ) {
            if (song.trackNumber > 0) {
                Text(
                    text = "${song.trackNumber}",
                    textAlign = TextAlign.End
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.height(56.dp)
                ) {
                    Text(
                        text = song.title,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Divider(thickness = 0.5.dp)
            }
        }
    }
}