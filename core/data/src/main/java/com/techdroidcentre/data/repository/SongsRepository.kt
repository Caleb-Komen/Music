package com.techdroidcentre.data.repository

import android.content.ContentResolver
import android.content.ContentUris
import android.provider.MediaStore
import com.techdroidcentre.data.model.Song
import javax.inject.Inject

interface SongsRepository {
    suspend fun fetchSongs()
}

class DefaultSongsRepository @Inject constructor(
    private val contentResolver: ContentResolver
): SongsRepository {
    var songs = emptyList<Song>()
        private set

    private val projection = arrayOf(
        MediaStore.Audio.Media._ID,
        MediaStore.Audio.Media.TITLE,
        MediaStore.Audio.Media.ALBUM_ID,
        MediaStore.Audio.Media.ALBUM,
        MediaStore.Audio.Media.ARTIST_ID,
        MediaStore.Audio.Media.ARTIST,
        MediaStore.Audio.Media.DURATION,
        MediaStore.Audio.Media.TRACK,
        MediaStore.Audio.Media.DATA,
    )
    override suspend fun fetchSongs() {
        val songsList = mutableListOf<Song>()
        contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            "${MediaStore.Audio.Media.IS_MUSIC} = ?",
            arrayOf("1"),
            null
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val albumIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
            val albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
            val artistIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST_ID)
            val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val trackNumberColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TRACK)
            val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)

            while(cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val title = cursor.getString(titleColumn)
                val albumId = cursor.getLong(albumIdColumn)
                val album = cursor.getString(albumColumn)
                val artistId = cursor.getLong(artistIdColumn)
                val artist = cursor.getString(artistColumn)
                val duration = cursor.getLong(durationColumn)
                val track = cursor.getInt(trackNumberColumn)
                val path = cursor.getString(dataColumn)
                val uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id).toString()
                val albumName = if (album == MediaStore.UNKNOWN_STRING) "Unknown Album" else album
                val artistName = if (artist == MediaStore.UNKNOWN_STRING) "Unknown Artist" else artist
                val trackNumber = if (track > 1000) track % 1000 else track
                songsList += Song(id, uri, title, albumId, albumName, artistId, artistName, duration, trackNumber, path)
            }
        }
        songs = songsList
    }

}