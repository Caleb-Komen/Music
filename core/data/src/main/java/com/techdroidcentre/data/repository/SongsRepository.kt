package com.techdroidcentre.data.repository

interface SongsRepository {
    suspend fun fetchSongs()
}
