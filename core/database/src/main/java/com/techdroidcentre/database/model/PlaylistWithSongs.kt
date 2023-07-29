package com.techdroidcentre.database.model

import androidx.room.Embedded
import androidx.room.Relation

data class PlaylistWithSongs(
    @Embedded
    val playlistEntity: PlaylistEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "playlist_id"
    )
    val songs: List<PlaylistSongEntity>
)
