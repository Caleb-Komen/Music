package com.techdroidcentre.playlists

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.techdroidcentre.designsystem.icon.MusicIcons
import com.techdroidcentre.designsystem.theme.MusicTheme
import com.techdroidcentre.model.Playlist
import com.techdroidcentre.playlists.component.NewOrUpdatePlaylistDialog
import com.techdroidcentre.playlists.component.PlaylistDropdownMenu
import com.techdroidcentre.playlists.util.DbTransaction.CREATE
import com.techdroidcentre.playlists.util.DbTransaction.UPDATE

@Composable
fun PlaylistsScreen(
    onBackPress: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PlaylistsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    Box(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        PlaylistsScreen(
            playlists = uiState.playlists,
            onCreatePlaylist = viewModel::savePlaylist,
            onRenamePlaylist = viewModel::updatePlaylist,
            onDeletePlaylist = viewModel::deletePlaylist,
            onBackPress = onBackPress
        )

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
fun PlaylistsScreen(
    playlists: List<Playlist>,
    onCreatePlaylist: (String) -> Unit,
    onRenamePlaylist: (Playlist) -> Unit,
    onDeletePlaylist: (Long) -> Unit,
    onBackPress: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column {
        PlaylistsTopBar(
            onCreatePlaylist = onCreatePlaylist,
            onBackPress = onBackPress
        )
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .height(56.dp)
        ) {
            items(items = playlists, key = { it.id }) { playlist ->
                PlaylistItem(
                    playlist = playlist,
                    onRenamePlaylist = onRenamePlaylist,
                    onDeletePlaylist = onDeletePlaylist
                )
            }
        }
    }
}

@Composable
fun PlaylistsTopBar(
    onCreatePlaylist: (String) -> Unit,
    onBackPress: () -> Unit,
    modifier: Modifier = Modifier
) {
    var playlistName by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }

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
            text = "Playlists",
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
        IconButton(onClick = { showDialog = true }) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "New Playlist"
            )
        }
    }

    if (showDialog) {
        NewOrUpdatePlaylistDialog(
            dbTransaction = CREATE,
            name = playlistName,
            onNameChange = { playlistName = it },
            onDismissRequest = {
                showDialog = false
                playlistName = ""
            },
            onCreatePlaylist = {
                onCreatePlaylist(it)
                showDialog = false
                playlistName = ""
            }
        )
    }
}

@Composable
fun PlaylistItem(
    playlist: Playlist,
    onRenamePlaylist: (Playlist) -> Unit,
    onDeletePlaylist: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var playlistName by remember { mutableStateOf(playlist.name) }

    Box(modifier = modifier.clickable {  }) {
        Column(
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    painter = painterResource(MusicIcons.queueMusic),
                    contentDescription = null,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(MaterialTheme.shapes.medium)
                        .background(Color.LightGray)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = playlist.name,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Box {
                    IconButton(onClick = { expanded = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = null
                        )
                    }
                    PlaylistDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        onRenamePlaylist = {
                            expanded = false
                            showDialog = true
                        },
                        onDeletePlaylist = {
                            onDeletePlaylist(playlist.id)
                            expanded = false
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Divider(thickness = 0.5.dp)
        }
    }

    if (showDialog) {
        NewOrUpdatePlaylistDialog(
            dbTransaction = UPDATE,
            name = playlistName,
            onNameChange = { playlistName = it },
            onDismissRequest = {
                showDialog = false
                playlistName = playlist.name
            },
            onCreatePlaylist = {
                onRenamePlaylist(Playlist(playlist.id, it))
                showDialog = false
                playlistName = it
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PlaylistItemPreview() {
    MusicTheme {
        PlaylistsScreen(playlists = listOf(
            Playlist(1, "Country"),
            Playlist(2, "Gospel"),
            Playlist(3, "Indie Folk"),
            Playlist(4, "Electronic"),
            Playlist(5, "Retro"),
            Playlist(6, "Disco Funk")
        ), {}, {}, {}, {})
    }
}

