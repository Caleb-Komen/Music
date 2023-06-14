package com.techdroidcentre.data.repository

import android.content.ContentResolver
import android.content.ContentUris
import android.provider.MediaStore
import com.techdroidcentre.data.model.ArtistInfo
import javax.inject.Inject

interface ArtistsRepository {
    suspend fun fetchArtists()
}

class DefaultArtistsRepository @Inject constructor(
    private val contentResolver: ContentResolver
): ArtistsRepository {
    var artists = emptyList<ArtistInfo>()
        private set

    override suspend fun fetchArtists() {
        val artistsList = mutableListOf<ArtistInfo>()
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
                artistsList += ArtistInfo(id, uri.toString(), name)
            }
        }
        artists = artistsList
    }
}