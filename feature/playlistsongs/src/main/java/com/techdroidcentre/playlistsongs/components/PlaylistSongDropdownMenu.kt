package com.techdroidcentre.playlistsongs.components

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun PlaylistSongDropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    onRemoveSong: () -> Unit,
    modifier: Modifier = Modifier
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        modifier = modifier
    ) {
        DropdownMenuItem(
            text = { Text(text = "Remove") },
            onClick = onRemoveSong
        )
    }
}