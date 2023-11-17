package com.techdroidcentre.favourites

import com.techdroidcentre.data.datastore.FavouriteSongsSortOption
import com.techdroidcentre.model.Song

data class FavouriteSongsUiState(
    val songs: List<Song> = emptyList(),
    val error: String = "",
    val loading: Boolean = false,
    val sortOption: FavouriteSongsSortOption = FavouriteSongsSortOption.TITLE
)