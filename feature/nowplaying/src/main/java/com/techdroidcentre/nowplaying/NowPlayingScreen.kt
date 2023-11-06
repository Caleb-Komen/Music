package com.techdroidcentre.nowplaying

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
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.techdroidcentre.designsystem.icon.MusicIcons
import com.techdroidcentre.model.Song
import java.util.concurrent.TimeUnit

@Composable
fun NowPlayingScreen(
    uiState: NowPlayingUiState,
    onPositionChange: (Float) -> Unit,
    playNextSong: () -> Unit,
    playPreviousSong: () -> Unit,
    playOrPause: () -> Unit,
    play: (String) -> Unit,
    togglePlaylistItems: () -> Unit,
    toggleShuffleMode: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (uiState.showPlaylistItems) {
            PlaylistItems(
                songs = uiState.playlistItems,
                nowPlaying = uiState.song,
                play = play,
                modifier = Modifier.weight(2f)
            )
        } else {
            PlaybackImage(
                song = uiState.song,
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.height(16.dp))
            PlaybackMetadata(song = uiState.song)
            Spacer(Modifier.height(16.dp))
            PlaybackOptions(
                shuffleModeEnabled = uiState.shuffleModeEnabled,
                toggleShuffleMode = toggleShuffleMode
            )
        }
        Spacer(Modifier.height(16.dp))
        PlaybackControls(
            isPlaying = uiState.isPlaying,
            duration = uiState.duration,
            position = uiState.position,
            showPlaylistItems = uiState.showPlaylistItems,
            onPositionChange = onPositionChange,
            playNextSong = playNextSong,
            playPreviousSong = playPreviousSong,
            playOrPause = playOrPause,
            togglePlaylistItems = togglePlaylistItems,
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
            .data(song.artworkData)
            .crossfade(true)
            .build(),
        error = painterResource(MusicIcons.defaultMusicNote),
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
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
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
    shuffleModeEnabled: Boolean,
    toggleShuffleMode: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = modifier.fillMaxWidth()
    ) {
        IconButton(
            onClick = toggleShuffleMode,
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_shuffle_24),
                contentDescription = "Shuffle",
                tint = if (shuffleModeEnabled) MaterialTheme.colorScheme.primary else LocalContentColor.current
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
    duration: Long,
    position: Long,
    showPlaylistItems: Boolean,
    onPositionChange: (Float) -> Unit,
    playNextSong: () -> Unit,
    playPreviousSong: () -> Unit,
    playOrPause: () -> Unit,
    togglePlaylistItems: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.Bottom) {
        Slider(
            value = position.toFloat(),
            onValueChange = onPositionChange,
            valueRange = 0f..duration.toFloat(),
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        Box(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        ) {
            Text(
                text = formatDuration(position),
                modifier = Modifier.align(Alignment.TopStart)
            )
            Text(
                text = formatDuration(duration),
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
                onClick = playPreviousSong
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_skip_previous_24),
                    contentDescription = "skip to previous",
                    modifier = Modifier.size(48.dp)
                )
            }
            IconButton(
                onClick = playOrPause
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
                onClick = playNextSong
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_skip_next_24),
                    contentDescription = "skip to next",
                    modifier = Modifier.size(48.dp)
                )
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(
                onClick = togglePlaylistItems,
                modifier = Modifier.size(48.dp)
                    .semantics(true){ contentDescription = "Toggle playlist items" }
            ) {
                if (showPlaylistItems) {
                    Icon(
                        painter = painterResource(id = R.drawable.list_box),
                        contentDescription = null
                    )
                } else {
                    Icon(
                        painter = painterResource(id = R.drawable.list_box_outline),
                        contentDescription = null
                    )
                }
            }
        }
    }
}

fun formatDuration(duration: Long): String {
    val minutes = TimeUnit.MILLISECONDS.toMinutes(duration)
    val totalSeconds = TimeUnit.MILLISECONDS.toSeconds(duration)
    val seconds = TimeUnit.MINUTES.toSeconds(minutes)
    val remainingSeconds = totalSeconds - seconds
    return String.format("%02d:%02d", minutes, remainingSeconds)
}