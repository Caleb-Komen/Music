package com.techdroidcentre.playlists.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.techdroidcentre.playlists.util.DbTransaction
import com.techdroidcentre.playlists.util.DbTransaction.CREATE

@Composable
fun NewOrUpdatePlaylistDialog(
    dbTransaction: DbTransaction,
    name: String,
    onNameChange: (String) -> Unit,
    onDismissRequest: () -> Unit,
    onCreatePlaylist: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val title = if (dbTransaction == CREATE) "New Playlist" else "Update playlist"
    Dialog(onDismissRequest = onDismissRequest) {
        Box(modifier = modifier.background(MaterialTheme.colorScheme.surface)) {
            Column(
                modifier = Modifier.padding(8.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = name,
                    onValueChange = onNameChange,
                    placeholder = {
                        Text(text = "Playlist Name")
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = onDismissRequest
                    ) {
                        Text(text = "Cancel")
                    }
                    Button(
                        onClick = { onCreatePlaylist(name) },
                        enabled = name.isNotEmpty()
                    ) {
                        Text(text = "OK")
                    }
                }
            }
        }
    }
}

@Composable
fun PlaylistDropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    onRenamePlaylist: () -> Unit,
    onDeletePlaylist: () -> Unit,
    modifier: Modifier = Modifier
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        modifier = modifier
    ) {
        DropdownMenuItem(
            text = { Text(text = "Rename") },
            onClick = onRenamePlaylist
        )
        DropdownMenuItem(
            text = { Text(text = "Delete") },
            onClick = onDeletePlaylist
        )
    }
}
