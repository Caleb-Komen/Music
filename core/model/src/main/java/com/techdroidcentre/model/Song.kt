package com.techdroidcentre.model

data class Song(
    val id: Long,
    val title: String,
    val albumId: Long,
    val album: String,
    val artistId: Long,
    val artist: String,
    val duration: Long,
    val trackNumber: Int
)
