package com.techdroidcentre.songs.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.techdroidcentre.data.datastore.SongsSortOption

@Composable
fun SortSongsDropdownMenu(
    expanded: Boolean,
    sortOption: String,
    onDismissRequest: () -> Unit,
    onSortSongs: (SongsSortOption) -> Unit,
    modifier: Modifier = Modifier
) {
    val menuItems = SongsSortOption.values().map {
        it.name.lowercase().replaceFirstChar { char -> char.titlecase() }
    }
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        modifier = modifier
    ) {
        menuItems.forEach { item ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (sortOption.lowercase() == item.lowercase()) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier
                            .size(24.dp)
                            .padding(start = 8.dp)
                    )
                } else {
                    Spacer(modifier = Modifier.size(24.dp).padding(start = 8.dp))
                }
                DropdownMenuItem(
                    text = { Text(text = item) },
                    onClick = {
                        onSortSongs(SongsSortOption.valueOf(item.uppercase()))
                    }
                )
            }
        }
    }
}