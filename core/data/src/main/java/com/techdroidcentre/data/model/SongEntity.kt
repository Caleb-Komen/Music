package com.techdroidcentre.data.model

data class SongEntity(
    val id: Long,
    val uri: String,
    val title: String,
    val albumId: Long,
    val album: String,
    val artistId: Long,
    val artist: String,
    val duration: Long,
    val trackNumber: Int,
    val path: String
)
