package com.techdroidcentre.player

import android.net.Uri
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.techdroidcentre.data.repository.DefaultSongsRepository
import com.techdroidcentre.data.repository.SongsRepository

const val ROOT_ID = "ROOT_ID"
const val SONGS_ID = "SONGS_ID"

@OptIn(androidx.media3.common.util.UnstableApi::class)
class MediaItemTree(private val songsRepository: SongsRepository) {
    private val children = mutableMapOf<String, List<MediaItem>>()

    init {
        val songs: MutableList<MediaItem> = mutableListOf()

        (songsRepository as DefaultSongsRepository).songs.forEach { song ->
            val mediaItem = buildMediaItem(
                song.id.toString(),
                isBrowsable = false,
                isPlayable = true,
                title = song.title,
                album = song.album,
                artist = song.artist,
                trackNumber = song.trackNumber
            )
            songs.add(mediaItem)
        }

        children[ROOT_ID] = listOf(
            buildMediaItem(ROOT_ID, isBrowsable = true, isPlayable = false)
        )
        children[SONGS_ID] = songs
    }

    operator fun get(id: String):  List<MediaItem> = children[id] ?: emptyList()

    private fun buildMediaItem(
        mediaId: String,
        isBrowsable: Boolean,
        isPlayable: Boolean,
        uri: String? = null,
        title: String? = null,
        album: String? = null,
        artist: String? = null,
        artUri: Uri? = null,
        trackNumber: Int? = null
    ): MediaItem {
        val mediaMetadata = MediaMetadata.Builder()
            .setTitle(title)
            .setAlbumTitle(album)
            .setArtist(artist)
            .setArtworkUri(artUri)
            .setTrackNumber(trackNumber)
            .setIsBrowsable(isBrowsable)
            .setIsPlayable(isPlayable)
            .build()

        return MediaItem.Builder()
            .setMediaId(mediaId)
            .setUri(uri)
            .setMediaMetadata(mediaMetadata)
            .build()
    }
}
