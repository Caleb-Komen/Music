package com.techdroidcentre.data.repository

import android.content.ContentResolver
import android.content.ContentUris
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.techdroidcentre.data.model.Album
import com.techdroidcentre.data.model.Song
import javax.inject.Inject

interface AlbumsRepository {
    suspend fun fetchAlbums()

    suspend fun fetchSongsForAlbum(albumId: Long)
}

class DefaultAlbumsRepository @Inject constructor(
    private val contentResolver: ContentResolver
): AlbumsRepository {
    var albumSongs = mutableMapOf<String, List<Song>>()
        private set
    var albums = emptyList<Album>()
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

    override suspend fun fetchAlbums() {
        val albumsList = mutableListOf<Album>()
        val projection = arrayOf(
            MediaStore.Audio.Albums._ID,
            MediaStore.Audio.Albums.ALBUM,
            MediaStore.Audio.Albums.ARTIST,
            MediaStore.Audio.Albums.NUMBER_OF_SONGS,
            MediaStore.Audio.Albums.FIRST_YEAR,
        )

        contentResolver.query(
            MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            null
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums._ID)
            val albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM)
            val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ARTIST)
            val noOfSongsColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.NUMBER_OF_SONGS)
            val firstYearColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.FIRST_YEAR)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val name = cursor.getString(albumColumn)
                val artist = cursor.getString(artistColumn)
                val noOfSongs = cursor.getInt(noOfSongsColumn)
                val year = cursor.getInt(firstYearColumn)
                val uri = ContentUris.withAppendedId(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, id)
                val artworkUri =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                        uri
                    else ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), id)
                albumsList += Album(id, uri.toString(), name, artist, artworkUri.toString(), noOfSongs, year)
            }
        }
        albumsList.forEach {
            fetchSongsForAlbum(it.id)
        }
        albums = albumsList
    }

    override suspend fun fetchSongsForAlbum(albumId: Long) {
        val songsList = mutableListOf<Song>()
        contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            "${MediaStore.Audio.Media.IS_MUSIC} = ? AND ${MediaStore.Audio.Media.ALBUM_ID} = ?",
            arrayOf("1", "$albumId"),
            null
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
            val artistIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST_ID)
            val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val trackNumberColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TRACK)
            val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)

            while(cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val title = cursor.getString(titleColumn)
                val album = cursor.getString(albumColumn)
                val artistId = cursor.getLong(artistIdColumn)
                val artist = cursor.getString(artistColumn)
                val duration = cursor.getLong(durationColumn)
                val trackNumber = cursor.getInt(trackNumberColumn)
                val path = cursor.getString(dataColumn)
                val uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id).toString()
                val albumName = if (album == MediaStore.UNKNOWN_STRING) "Unknown Album" else album
                val artistName = if (artist == MediaStore.UNKNOWN_STRING) "Unknown Artist" else artist
                songsList += Song(id, uri, title, albumId, albumName, artistId, artistName, duration, trackNumber, path)
            }
        }
        albumSongs[albumId.toString()] = songsList
    }
}