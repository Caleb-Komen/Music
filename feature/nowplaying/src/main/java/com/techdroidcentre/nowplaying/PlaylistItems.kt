package com.techdroidcentre.nowplaying

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.techdroidcentre.designsystem.icon.MusicIcons
import com.techdroidcentre.model.Song

@Composable
fun PlaylistItems(
    songs: List<Song>,
    nowPlaying: Song,
    play: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (songs.isNotEmpty()) {
        val previousSongs = songs.subList(0, songs.indexOf(nowPlaying))
        val nextSongs = songs.subList(songs.indexOf(nowPlaying) + 1, songs.size)
        val lazyListState = rememberLazyListState()
        LaunchedEffect(lazyListState) {
            lazyListState.animateScrollToItem(songs.indexOf(nowPlaying))
        }
        LazyColumn(
            modifier = modifier
                .fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 24.dp),
            state = lazyListState
        ) {
            items(items = previousSongs, key = { it.id }) { song ->
                SongItem(song = song, play = play)
            }

            item(key = nowPlaying.id) {
                Column {
                    Spacer(Modifier.height(16.dp))
                    SongItem(song = nowPlaying, play = play)
                    Spacer(Modifier.height(16.dp))
                }
            }

            item {
                Text(
                    text = "Playing Next",
                    fontWeight = FontWeight.Bold
                )
            }

            items(items = nextSongs, key = { it.id }) { song ->
                SongItem(song = song, play = play)
            }
        }
    }
}

@Composable
fun SongItem(
    song: Song,
    play: (id: String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.clickable { play(song.id.toString()) }
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