package com.techdroidcentre.nowplaying

import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.techdroidcentre.common.getThumbnail
import com.techdroidcentre.model.Song

@Composable
fun NowPlayingScreen(
    song: Song,
    nextSong: () -> Unit,
    previousSong: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PlaybackImage(
            song = song,
            modifier = Modifier.weight(1f)
        )
        Spacer(Modifier.height(16.dp))
        PlaybackMetadata(song = song)
        Spacer(Modifier.height(16.dp))
        PlaybackOptions()
        Spacer(Modifier.height(16.dp))
        PlaybackControls(
            isPlaying = false,
            nextSong = nextSong,
            previousSong = previousSong,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun PlaybackImage(
    song: Song,
    modifier: Modifier = Modifier
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
        modifier = modifier
            .sizeIn(maxWidth = 500.dp, maxHeight = 500.dp)
            .aspectRatio(1f)
            .clip(MaterialTheme.shapes.small),
        contentScale = ContentScale.Crop
    )
}

@Composable
fun PlaybackMetadata(
    song: Song,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = song.title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = song.artist,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun PlaybackOptions(
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = modifier.fillMaxWidth()
    ) {
        IconButton(
            onClick = {},
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_shuffle_24),
                contentDescription = "Shuffle"
            )
        }
        IconButton(
            onClick = {},
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_playlist_add_24),
                contentDescription = "Add to playlist"
            )
        }
        IconButton(
            onClick = {},
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_repeat_24),
                contentDescription = "Repeat"
            )
        }
    }
}

@Composable
fun PlaybackControls(
    isPlaying: Boolean,
    nextSong: () -> Unit,
    previousSong: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Slider(
            value = 0.5f,
            onValueChange = {},
            valueRange = 0f..1f
        )
        Box(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        ) {
            Text(
                text = "2:00",
                modifier = Modifier.align(Alignment.TopStart)
            )
            Text(
                text = "4:00",
                modifier = Modifier.align(Alignment.TopEnd)
            )

        }
        Spacer(Modifier.height(8.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(
                onClick = previousSong
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_skip_previous_24),
                    contentDescription = "skip to previous",
                    modifier = Modifier.size(48.dp)
                )
            }
            IconButton(
                onClick = {}
            ) {
                if (isPlaying) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_pause_24),
                        contentDescription = "pause",
                        modifier = Modifier.size(48.dp)
                    )
                } else {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_play_arrow_24),
                        contentDescription = "play",
                        modifier = Modifier.size(48.dp)
                    )
                }
            }
            IconButton(
                onClick = nextSong
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_skip_next_24),
                    contentDescription = "skip to next",
                    modifier = Modifier.size(48.dp)
                )
            }
        }
    }
}