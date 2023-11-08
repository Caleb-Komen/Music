package com.techdroidcentre.data.repository

import com.techdroidcentre.model.TopAlbum
import kotlinx.coroutines.flow.Flow

interface TopAlbumsRepository {
    fun getTopAlbums(): Flow<List<TopAlbum>>

    suspend fun getTopAlbum(albumId: Long): TopAlbum?

    suspend fun addTopAlbum(topAlbum: TopAlbum)

    suspend fun deleteTopAlbum(albumId: Long)
}