package com.techdroidcentre.data.repository

interface ArtistsRepository {
    suspend fun fetchArtists()

    suspend fun fetchArtistAlbums(artistId: String)
}
