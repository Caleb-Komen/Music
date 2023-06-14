package com.techdroidcentre.data.repository

interface AlbumsRepository {
    suspend fun fetchAlbums()

    suspend fun fetchSongsForAlbum(albumId: Long)
}
