package com.techdroidcentre.data.model

data class Album(
    val id: Long,
    val uri: String,
    val name: String,
    val artist: String,
    val artworkUri: String,
    val noOfSongs: Int,
    val year: Int,
)
