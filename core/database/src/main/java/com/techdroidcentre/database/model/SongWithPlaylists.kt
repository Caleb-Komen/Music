package com.techdroidcentre.database.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class SongWithPlaylists(
    @Embedded
    val playlistSongEntity: PlaylistSongEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(PlaylistSongCrossRef::class, "song_id", "playlist_id")
    )
    val playlists: List<PlaylistEntity>
)
