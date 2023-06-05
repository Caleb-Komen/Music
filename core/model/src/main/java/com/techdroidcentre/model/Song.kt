package com.techdroidcentre.model

data class Song(
    val id: Long = -1L,
    val uri: String = "",
    val title: String  = "",
    val album: String = "",
    val artist: String = "",
    val trackNumber: Int = 0,
    val artworkUri: String = "",
    val artworkData: ByteArray? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Song

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
