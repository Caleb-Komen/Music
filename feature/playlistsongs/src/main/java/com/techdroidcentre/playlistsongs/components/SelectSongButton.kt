package com.techdroidcentre.playlistsongs.components

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.techdroidcentre.designsystem.theme.MusicTheme

@Composable
fun SelectSongButton(
    selected: Boolean,
    modifier: Modifier = Modifier
) {
    val icon = if (selected) Icons.Filled.Done else Icons.Filled.Add
    val iconColor = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary
    val borderColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
    val backgroundColor = if (selected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onPrimary
    }
    Surface(
        color = backgroundColor,
        shape = CircleShape,
        border = BorderStroke(1.dp, borderColor),
        modifier = modifier.size(36.dp, 36.dp)
    ) {
        Image(
            imageVector = icon,
            colorFilter = ColorFilter.tint(iconColor),
            modifier = Modifier.padding(8.dp),
            contentDescription = null
        )
    }
}

@Preview("Off")
@Preview("Off (dark)", uiMode = UI_MODE_NIGHT_YES)
@Composable
fun SelectSongButtonPreviewOff() {
    SelectSongButtonPreviewTemplate(
        selected = false
    )
}

@Preview("On")
@Preview("On (dark)", uiMode = UI_MODE_NIGHT_YES)
@Composable
fun SelectSongButtonPreviewOn() {
    SelectSongButtonPreviewTemplate(
        selected = true
    )
}

@Composable
private fun SelectSongButtonPreviewTemplate(
    selected: Boolean
) {
    MusicTheme {
        Surface {
            SelectSongButton(
                modifier = Modifier.padding(32.dp),
                selected = selected
            )
        }
    }
}