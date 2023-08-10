package com.techdroidcentre.artistdetails.components

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
import com.techdroidcentre.data.datastore.ArtistAlbumsSortOption

@Composable
fun SortAlbumsDropdownMenu(
    expanded: Boolean,
    sortOption: String,
    onDismissRequest: () -> Unit,
    onSortAlbums: (ArtistAlbumsSortOption) -> Unit,
    modifier: Modifier = Modifier
) {
    val menuItems = ArtistAlbumsSortOption.values()

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        modifier = modifier
    ) {
        menuItems.forEach { item ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (sortOption.lowercase() == item.name.lowercase()) {
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
                val menuItem = when (item) {
                    ArtistAlbumsSortOption.TITLE -> "Title"
                    ArtistAlbumsSortOption.YEAR_ASCENDING -> "Year Ascending"
                    ArtistAlbumsSortOption.YEAR_DESCENDING -> "Year Descending"
                }
                DropdownMenuItem(
                    text = { Text(text = menuItem) },
                    onClick = {
                        onSortAlbums(item)
                    }
                )
            }
        }
    }
}