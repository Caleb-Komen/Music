package com.techdroidcentre.database.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class PlaylistWithSongs(
    @Embedded
    val playlistEntity: PlaylistEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(PlaylistSongCrossRef::class, "playlist_id", "song_id")
    )
    val songs: List<PlaylistSongEntity>
)
