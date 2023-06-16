package com.techdroidcentre.data.repository

import android.content.ContentResolver
import android.content.ContentUris
import android.provider.MediaStore
import com.techdroidcentre.data.model.ArtistEntity
import javax.inject.Inject

class DefaultArtistsRepository @Inject constructor(
    private val contentResolver: ContentResolver
): ArtistsRepository {
    var artists = emptyList<ArtistEntity>()
        private set
    var artistAlbums = mutableMapOf<String, List<String>>()
        private set
    override suspend fun fetchArtists() {
        val artistsList = mutableListOf<ArtistEntity>()
        val projection = arrayOf(
            MediaStore.Audio.Artists._ID,
            MediaStore.Audio.Artists.ARTIST
        )

        contentResolver.query(
            MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            MediaStore.Audio.Artists.ARTIST
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Artists._ID)
            val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Artists.ARTIST)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val name = cursor.getString(artistColumn)
                val uri = ContentUris.withAppendedId(MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI, id)
                artistsList += ArtistEntity(id, uri.toString(), name)
            }
        }
        artistsList.forEach { fetchArtistAlbums(it.id.toString()) }
        artists = artistsList
    }

    override suspend fun fetchArtistAlbums(artistId: String) {
        val albumsIds = mutableListOf<String>()
        val projection = arrayOf(
            MediaStore.Audio.Albums._ID
        )

        contentResolver.query(
            MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
            projection,
            "${MediaStore.Audio.Albums.ARTIST_ID} = ?",
            arrayOf(artistId),
            null
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums._ID)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                albumsIds += id.toString()
            }
            artistAlbums[artistId] = albumsIds
        }
    }
}