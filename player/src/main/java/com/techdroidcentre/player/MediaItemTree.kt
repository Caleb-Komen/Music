package com.techdroidcentre.player

import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.techdroidcentre.data.repository.AlbumsRepository
import com.techdroidcentre.data.repository.ArtistsRepository
import com.techdroidcentre.data.repository.DefaultAlbumsRepository
import com.techdroidcentre.data.repository.DefaultArtistsRepository
import com.techdroidcentre.data.repository.DefaultSongsRepository
import com.techdroidcentre.data.repository.SongsRepository

const val ROOT_ID = "ROOT_ID"
const val SONGS_ID = "SONGS_ID"
const val ALBUMS_ID = "ALBUMS_ID"
const val ARTISTS_ID = "ARTISTS_ID"

@OptIn(androidx.media3.common.util.UnstableApi::class)
class MediaItemTree(
    songsRepository: SongsRepository,
    albumsRepository: AlbumsRepository,
    artistsRepository: ArtistsRepository
) {
    private val treeNode = mutableMapOf<String, MediaItemNode>()

    init {
        treeNode[ROOT_ID] = MediaItemNode(
            buildMediaItem(ROOT_ID, isBrowsable = true, isPlayable = false)
        )
        treeNode[SONGS_ID] = MediaItemNode(
            buildMediaItem(SONGS_ID, isBrowsable = true, isPlayable = false)
        )
        treeNode[ALBUMS_ID] = MediaItemNode(
            buildMediaItem(ALBUMS_ID, isBrowsable = true, isPlayable = false)
        )
        treeNode[ARTISTS_ID] = MediaItemNode(
            buildMediaItem(ARTISTS_ID, isBrowsable = true, isPlayable = false)
        )
        treeNode[ROOT_ID]!!.addChild(SONGS_ID)
        treeNode[ROOT_ID]!!.addChild(ALBUMS_ID)
        treeNode[ROOT_ID]!!.addChild(ARTISTS_ID)
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
                    artworkUri = Uri.parse(song.uri),
                    artworkData = getArtworkData(song.path)
                )
            )
            treeNode[SONGS_ID]!!.addChild(id)
        }

        (albumsRepository as DefaultAlbumsRepository).albums.forEach { album ->
            val id = album.id.toString()
            treeNode[id] = MediaItemNode(
                buildMediaItem(
                    mediaId = id,
                    uri = album.uri,
                    isBrowsable = true,
                    isPlayable = false,
                    title = album.name,
                    artist = album.artist,
                    artworkUri = Uri.parse(album.artworkUri),
                    totalTrackCount = album.noOfSongs,
                    recordingYear = album.year
                )
            )
            treeNode[ALBUMS_ID]!!.addChild(id)

            val songs = albumsRepository.albumSongs[id] ?: emptyList()
            songs.forEach { song ->
                val item = treeNode[song.id.toString()]?.mediaItem
                if (item != null){
                    treeNode[id]!!.addChild(item.mediaId)
                }
            }
        }

        (artistsRepository as DefaultArtistsRepository).artists.forEach { artist ->
            // For some reasons there a bug when fetching artist albums in some API level e.g 24.
            // So prepend artist id with 'artist-' to avoid the bug.
            // The issue might be because some artist id and album id are the same(Not sure) which
            // result in incorrect data being fetched.
            val id = "artist-${artist.id}"
            treeNode[id] = MediaItemNode(
                buildMediaItem(
                    mediaId = id,
                    uri = artist.uri,
                    isBrowsable = true,
                    isPlayable = false,
                    title = artist.name
                )
            )
            treeNode[ARTISTS_ID]!!.addChild(id)

            val albumsIds = artistsRepository.artistAlbums[artist.id.toString()] ?: emptyList()
            albumsIds.forEach { albumId ->
                val item = treeNode[albumId]?.mediaItem
                if (item != null) {
                    treeNode[id]!!.addChild(item.mediaId)
                }
            }
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
        artworkUri: Uri? = null,
        trackNumber: Int? = null,
        artworkData: ByteArray? = null,
        totalTrackCount: Int? = null,
        recordingYear: Int? = null
    ): MediaItem {
        val mediaMetadata = MediaMetadata.Builder()
            .setTitle(title)
            .setAlbumTitle(album)
            .setArtist(artist)
            .setArtworkUri(artworkUri)
            .setArtworkData(artworkData, MediaMetadata.PICTURE_TYPE_FRONT_COVER)
            .setTrackNumber(trackNumber)
            .setIsBrowsable(isBrowsable)
            .setIsPlayable(isPlayable)
            .setTotalTrackCount(totalTrackCount)
            .setRecordingYear(recordingYear)
            .build()

        return MediaItem.Builder()
            .setMediaId(mediaId)
            .setUri(uri)
            .setMediaMetadata(mediaMetadata)
            .build()
    }

    fun getMediaItem(id: String): MediaItem? {
        return treeNode[id]?.mediaItem
    }

    private fun getArtworkData(path: String): ByteArray? {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(path)
        val art = retriever.embeddedPicture
        retriever.release()
        return art
    }
}
