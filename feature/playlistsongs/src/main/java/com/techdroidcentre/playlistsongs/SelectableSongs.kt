package com.techdroidcentre.playlistsongs

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.techdroidcentre.designsystem.icon.MusicIcons
import com.techdroidcentre.designsystem.theme.MusicTheme
import com.techdroidcentre.model.Song
import com.techdroidcentre.playlistsongs.components.SelectSongButton

@Composable
fun SelectableSongs(
    songs: List<Song>,
    selectedSongs: Set<Long>,
    onSelect: (Long) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onCancel) {
                Text(text = "Cancel", color = MaterialTheme.colorScheme.onSurface)
            }
            Spacer(modifier = Modifier.weight(1f))
            TextButton(onClick = onSave) {
                Text(text = "Done", color = MaterialTheme.colorScheme.onSurface)
            }
        }
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            item { Spacer(modifier = Modifier.height(24.dp)) }
            items(items = songs) { song ->
                SelectableSongItem(
                    song = song,
                    selected = selectedSongs.contains(song.id),
                    onSelect = onSelect
                )
            }
        }
    }
}

@Composable
fun SelectableSongItem(
    song: Song,
    selected: Boolean,
    onSelect: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.clickable { onSelect(song.id) }
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 24.dp)
        ) {
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
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
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
            Spacer(modifier = Modifier.width(8.dp))
            SelectSongButton(selected = selected)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Divider(thickness = 0.5.dp, modifier = Modifier.padding(start = 88.dp, end = 24.dp))
    }
}

@Preview("SelectableSongs")
@Preview("SelectableSongs (dark)", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun SelectableSongsOffPreview() {
    SelectableSongsPreview()
}

@Composable
fun SelectableSongsPreview() {
    MusicTheme {
        Surface {
            SelectableSongs(
                songs = listOf(
                    Song(id = 10, title = "We got love", album = "We got love", artist = "Don Williams"),
                    Song(id = 11, title = "Jolene", album = "Jolene", artist = "Dolly Parton"),
                    Song(id = 12, title = "Breathe", album = "Breathe", artist = "Faith Hill"),
                    Song(id = 13, title = "Remember when", album = "Remember when", artist = "Alan Jackson"),
                    Song(id = 14, title = "Amazed", album = "Amazed", artist = "Lonestar"),
                ),
                selectedSongs = setOf(10, 12, 14), {}, {}, {}
            )
        }
    }
}
