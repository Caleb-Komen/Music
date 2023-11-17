package com.techdroidcentre.data.repository

import com.techdroidcentre.model.FavouriteSong
import kotlinx.coroutines.flow.Flow

interface FavouriteSongsRepository {
    fun getFavouriteSongs(): Flow<List<FavouriteSong>>

    fun isFavourite(songId: Long): Flow<Boolean>

    suspend fun toggleFavourite(songId: Long)
}