package com.techdroidcentre.data.mapper

import com.techdroidcentre.database.model.PlaylistEntity
import com.techdroidcentre.model.Playlist

fun Playlist.toEntity() = PlaylistEntity(
    id = id,
    name = name
)

fun PlaylistEntity.toModel() = Playlist(
    id = id,
    name = name
)