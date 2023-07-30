package com.techdroidcentre.playlistsongs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.techdroidcentre.designsystem.component.AudioWave
import com.techdroidcentre.designsystem.icon.MusicIcons
import com.techdroidcentre.designsystem.theme.MusicTheme
import com.techdroidcentre.model.Playlist
import com.techdroidcentre.model.Song
import com.techdroidcentre.playlistsongs.components.PlaylistSongDropdownMenu

@Composable
fun PlaylistSongsScreen(
    onBackPress: () -> Unit,
    viewModel: PlaylistSongsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showSelectableSongs by remember { mutableStateOf(false) }

    PlaylistSongsScreen(
        uiState = uiState,
        play = viewModel::play,
        shuffle = viewModel::shuffle,
        playOrPause = { viewModel.playOrPause(it.id.toString()) },
        onRemoveSong = viewModel::removeSong,
        onBackPress = onBackPress,
        onShowSelectableSongs = { showSelectableSongs = true }
    )

    AnimatedVisibility(visible = showSelectableSongs) {
        SelectableSongs(
            songs = uiState.allSongs,
            selectedSongs = uiState.selectedSongs,
            onSelect = viewModel::updateSelectedSongs,
            onSave = {
                viewModel.insertSongs()
                viewModel.updateSelectedSongs(0, true)
                showSelectableSongs = false
            },
            onCancel= {
                viewModel.updateSelectedSongs(0, true)
                showSelectableSongs = false
            },
            modifier = Modifier.statusBarsPadding()
        )
    }
}

@Composable
fun PlaylistSongsScreen(
    uiState: PlaylistSongsUiState,
    play: () -> Unit,
    shuffle: () -> Unit,
    playOrPause: (Song) -> Unit,
    onRemoveSong: (Long) -> Unit,
    onBackPress: () -> Unit,
    onShowSelectableSongs: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize()
            .statusBarsPadding()
    ) {
        PlaylistSongsTopBar(
            playlistName = uiState.playlist.name,
            onBackPress = onBackPress,
            onShowSelectableSongs = onShowSelectableSongs
        )
        Spacer(modifier = Modifier.height(24.dp))
        PlaylistMediaControls(
            play = play,
            shuffle = shuffle,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            item { Spacer(modifier = Modifier.height(24.dp)) }
            items(items = uiState.playlistSongs, key = { it.id }) { song ->
                SongItem(
                    song = song,
                    playingSongId = uiState.playingSongId,
                    isSongPlaying = uiState.isSongPlaying,
                    playOrPause = playOrPause,
                    onRemoveSong = onRemoveSong
                )
            }
        }
    }

}

@Composable
fun PlaylistSongsTopBar(
    playlistName: String,
    onBackPress: () -> Unit,
    onShowSelectableSongs: () -> Unit,
    modifier: Modifier = Modifier
) {
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
            text = playlistName,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f)
        )
        IconButton(onClick = onShowSelectableSongs) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Songs"
            )
        }
    }
}

@Composable
fun PlaylistMediaControls(
    play: () -> Unit,
    shuffle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
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
    playingSongId: String,
    isSongPlaying: Boolean,
    playOrPause: (Song) -> Unit,
    onRemoveSong: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
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
            Column(modifier = Modifier.padding(end = 24.dp).weight(1f)) {
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
            Box {
                IconButton(onClick = { expanded = true }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = null
                    )
                }
                PlaylistSongDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    onRemoveSong = {
                        onRemoveSong(song.id)
                        expanded = false
                    }
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Divider(thickness = 0.5.dp, modifier = Modifier.padding(horizontal = 24.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun PlaylistSongsScreenPreview() {
    MusicTheme {
        PlaylistSongsScreen(
            PlaylistSongsUiState(
                playlist = Playlist(1L, "Country Ballads"),
                playlistSongs = listOf(
                    Song(id = 10, title = "We got love", album = "We got love", artist = "Don Williams"),
                    Song(id = 11, title = "Jolene", album = "Jolene", artist = "Dolly Parton"),
                    Song(id = 12, title = "Breathe", album = "Breathe", artist = "Faith Hill"),
                    Song(id = 13, title = "Remember when", album = "Remember when", artist = "Alan Jackson"),
                    Song(id = 14, title = "Amazed", album = "Amazed", artist = "Lonestar"),
                ),
                selectedSongs = setOf(10, 12, 14)
            ), {}, {}, {}, {}, {}, {}
        )
    }
}