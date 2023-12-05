package com.techdroidcentre.designsystem.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

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