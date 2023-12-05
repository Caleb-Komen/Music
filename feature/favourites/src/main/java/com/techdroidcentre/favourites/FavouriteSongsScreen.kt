package com.techdroidcentre.favourites

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.techdroidcentre.data.datastore.FavouriteSongsSortOption
import com.techdroidcentre.designsystem.component.MediaControls
import com.techdroidcentre.designsystem.icon.MusicIcons
import com.techdroidcentre.favourites.components.FavouriteSongDropdownMenu
import com.techdroidcentre.favourites.components.SortSongsDropdownMenu
import com.techdroidcentre.model.Song

@Composable
fun FavouriteSongsScreen(
    onBackPress: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FavouriteSongsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    FavouriteSongsScreen(
        uiState = uiState,
        play = viewModel::play,
        shuffle = viewModel::shuffle,
        playOrPause = { viewModel.playOrPause(it.id.toString()) },
        onRemoveSong = viewModel::removeSong,
        onBackPress = onBackPress,
        onSortSongs = viewModel::setFavouriteSongsSortOption,
        modifier = modifier
    )
}

@Composable
fun FavouriteSongsScreen(
    uiState: FavouriteSongsUiState,
    play: () -> Unit,
    shuffle: () -> Unit,
    playOrPause: (Song) -> Unit,
    onRemoveSong: (Long) -> Unit,
    onBackPress: () -> Unit,
    onSortSongs: (FavouriteSongsSortOption) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        FavouriteSongsTopBar(
            sortOption = uiState.sortOption.name,
            onBackPress = onBackPress,
            onSortSongs = onSortSongs
        )
        Spacer(modifier = Modifier.height(24.dp))
        MediaControls(
            play = play,
            shuffle = shuffle,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
        Box(contentAlignment = Alignment.Center) {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                item { Spacer(modifier = Modifier.height(24.dp)) }
                items(items = uiState.songs, key = { it.id }) { song ->
                    SongItem(
                        song = song,
                        playOrPause = playOrPause,
                        onRemoveSong = onRemoveSong
                    )
                }
            }
            if (!uiState.loading && uiState.songs.isEmpty()) EmptyFavouriteSongsScreen(modifier = Modifier.padding(24.dp))
        }
    }
}

@Composable
fun FavouriteSongsTopBar(
    sortOption: String,
    onBackPress: () -> Unit,
    onSortSongs: (FavouriteSongsSortOption) -> Unit,
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
            text = "Favourites",
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f)
        )
        Box(modifier = Modifier.padding(horizontal = 8.dp)) {
            IconButton(onClick = { expanded = true }) {
                Icon(
                    painter = painterResource(MusicIcons.sort),
                    contentDescription = "Sort Songs"
                )
            }
            SortSongsDropdownMenu(
                expanded = expanded,
                sortOption = sortOption,
                onDismissRequest = { expanded = false },
                onSortSongs = {
                    onSortSongs(it)
                    expanded = false
                }
            )
        }
    }
}

@Composable
fun EmptyFavouriteSongsScreen(modifier: Modifier = Modifier) {
    Text(
        text = "It's a bit lonely here. No favourites found.",
        style = MaterialTheme.typography.titleMedium,
        modifier = modifier
    )
}

@Composable
fun SongItem(
    song: Song,
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
            Box(modifier = Modifier.padding(horizontal = 8.dp)) {
                IconButton(onClick = { expanded = true }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = null
                    )
                }
                FavouriteSongDropdownMenu(
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