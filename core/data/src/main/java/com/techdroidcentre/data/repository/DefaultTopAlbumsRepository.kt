package com.techdroidcentre.data.repository

import com.techdroidcentre.data.mapper.toEntity
import com.techdroidcentre.data.mapper.toModel
import com.techdroidcentre.database.dao.TopAlbumsDao
import com.techdroidcentre.model.TopAlbum
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DefaultTopAlbumsRepository@Inject constructor(
    private val topAlbumsDao: TopAlbumsDao
): TopAlbumsRepository {
    override fun getTopAlbums(): Flow<List<TopAlbum>> {
        return topAlbumsDao.getTopAlbums().map { topAlbums -> topAlbums.map { it.toModel() } }
    }

    override suspend fun getTopAlbum(albumId: Long): TopAlbum? {
        return topAlbumsDao.getTopAlbum(albumId)?.toModel()
    }

    override suspend fun addTopAlbum(topAlbum: TopAlbum) {
        topAlbumsDao.addTopAlbum(topAlbum.toEntity())
    }

    override suspend fun deleteTopAlbum(albumId: Long) {
        topAlbumsDao.deleteTopAlbum(albumId)
    }
}