package com.techdroidcentre.data.repository

import com.techdroidcentre.data.mapper.toEntity
import com.techdroidcentre.data.mapper.toModel
import com.techdroidcentre.database.dao.FavouriteSongsDao
import com.techdroidcentre.model.FavouriteSong
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DefaultFavouriteSongsRepository @Inject constructor(
    private val favouriteSongsDao: FavouriteSongsDao
): FavouriteSongsRepository {
    override fun getFavouriteSongs(): Flow<List<FavouriteSong>> {
        return favouriteSongsDao.getFavouriteSongs().map { songs ->
            songs.map { it.toModel() }
        }
    }

    override fun isFavourite(songId: Long): Flow<Boolean> {
        return favouriteSongsDao.isFavourite(songId)
    }

    override suspend fun toggleFavourite(songId: Long) {
        val isFavourite = favouriteSongsDao.isFavourite(songId).first()
        if (isFavourite) deleteFavouriteSong(songId) else addFavouriteSong(FavouriteSong(songId))
    }

    private suspend fun addFavouriteSong(favouriteSong: FavouriteSong) {
        favouriteSongsDao.addFavouriteSong(favouriteSong.toEntity())
    }

    private suspend fun deleteFavouriteSong(songId: Long) {
        favouriteSongsDao.deleteFavouriteSong(songId)
    }
}