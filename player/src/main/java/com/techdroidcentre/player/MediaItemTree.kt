package com.techdroidcentre.player

import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.techdroidcentre.data.repository.DefaultSongsRepository
import com.techdroidcentre.data.repository.SongsRepository

const val ROOT_ID = "ROOT_ID"
const val SONGS_ID = "SONGS_ID"

@OptIn(androidx.media3.common.util.UnstableApi::class)
class MediaItemTree (songsRepository: SongsRepository) {
    private val treeNode = mutableMapOf<String, MediaItemNode>()

    init {
        treeNode[ROOT_ID] = MediaItemNode(
            buildMediaItem(ROOT_ID, isBrowsable = true, isPlayable = false)
        )
        treeNode[SONGS_ID] = MediaItemNode(
            buildMediaItem(SONGS_ID, isBrowsable = true, isPlayable = false)
        )
        treeNode[ROOT_ID]!!.addChild(SONGS_ID)
        (songsRepository as DefaultSongsRepository).songs.forEach { song ->
            val id = song.id.toString()
            treeNode[id] = MediaItemNode(
                buildMediaItem(
                    mediaId = id,
                    uri = song.uri,
                    isBrowsable = false,
                    isPlayable = true,
                    title = song.title,
                    album = song.album,
                    artist = song.artist,
                    trackNumber = song.trackNumber,
                    artUri = Uri.parse(song.uri),
                    artworkData = getArtworkData(song.path)
                )
            )
            treeNode[SONGS_ID]!!.addChild(id)
        }
    }

    inner class MediaItemNode(val mediaItem: MediaItem) {
        private val children: MutableList<MediaItem> = mutableListOf()

        fun addChild(childId: String) {
            children.add(treeNode[childId]!!.mediaItem)
        }

        fun getChildren(): List<MediaItem> {
            return children
        }
    }

    operator fun get(id: String):  MediaItemNode? = treeNode[id]

    private fun buildMediaItem(
        mediaId: String,
        isBrowsable: Boolean,
        isPlayable: Boolean,
        uri: String? = null,
        title: String? = null,
        album: String? = null,
        artist: String? = null,
        artUri: Uri? = null,
        trackNumber: Int? = null,
        artworkData: ByteArray? = null
    ): MediaItem {
        val mediaMetadata = MediaMetadata.Builder()
            .setTitle(title)
            .setAlbumTitle(album)
            .setArtist(artist)
            .setArtworkUri(artUri)
            .setArtworkData(artworkData, MediaMetadata.PICTURE_TYPE_FRONT_COVER)
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

    private fun getArtworkData(path: String): ByteArray? {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(path)
        val art = retriever.embeddedPicture
        retriever.release()
        return art
    }
}
