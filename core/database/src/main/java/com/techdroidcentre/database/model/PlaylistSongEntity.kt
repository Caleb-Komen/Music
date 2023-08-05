package com.techdroidcentre.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlist_songs",)
data class PlaylistSongEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Long,
    @ColumnInfo(name = "uri")
    val uri: String,
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "album")
    val album: String,
    @ColumnInfo(name = "artist")
    val artist: String,
    @ColumnInfo(name = "track_number")
    val trackNumber: Int,
    @ColumnInfo(name = "artwork_uri")
    val artworkUri: String,
    @ColumnInfo(name = "artwork_data")
    val artworkData: ByteArray?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PlaylistSongEntity

        if (id != other.id) return false
        if (uri != other.uri) return false
        if (title != other.title) return false
        if (album != other.album) return false
        if (artist != other.artist) return false
        if (trackNumber != other.trackNumber) return false
        if (artworkUri != other.artworkUri) return false
        if (artworkData != null) {
            if (other.artworkData == null) return false
            if (!artworkData.contentEquals(other.artworkData)) return false
        } else if (other.artworkData != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + uri.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + album.hashCode()
        result = 31 * result + artist.hashCode()
        result = 31 * result + trackNumber
        result = 31 * result + artworkUri.hashCode()
        result = 31 * result + (artworkData?.contentHashCode() ?: 0)
        return result
    }
}
