package com.techdroidcentre.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "music")

class MusicDataStore @Inject constructor(@ApplicationContext val context: Context) {
    private val songsSortOptionKey = intPreferencesKey("songs_sort_option")
    private val playlistSongsSortOptionKey = intPreferencesKey("playlist_songs_sort_option")
    private val artistAlbumsSortOptionKey = intPreferencesKey("artist_albums_sort_option")
    private val favouriteSongsSortOptionKey = intPreferencesKey("favourite_songs_sort_option")
    private val shuffleModeKey = intPreferencesKey("shuffle_mode")
    private val repeatModeKey = intPreferencesKey("repeat_mode")

    suspend fun setSongsSortOption(songsSortOption: SongsSortOption) {
        context.dataStore.edit { preferences ->
            preferences[songsSortOptionKey] = songsSortOption.ordinal
        }
    }

    suspend fun setPlaylistSongsSortOption(playlistSongsSortOption: PlaylistSongsSortOption) {
        context.dataStore.edit { preferences ->
            preferences[playlistSongsSortOptionKey] = playlistSongsSortOption.ordinal
        }
    }

    suspend fun setArtistAlbumsSortOption(artistAlbumsSortOption: ArtistAlbumsSortOption) {
        context.dataStore.edit { preferences ->
            preferences[artistAlbumsSortOptionKey] = artistAlbumsSortOption.ordinal
        }
    }

    suspend fun setFavouriteSongsSortOption(favouriteSongsSortOption: FavouriteSongsSortOption) {
        context.dataStore.edit { preferences ->
            preferences[favouriteSongsSortOptionKey] = favouriteSongsSortOption.ordinal
        }
    }

    suspend fun setShuffleMode(shuffleMode: ShuffleMode) {
        context.dataStore.edit { preferences ->
            preferences[shuffleModeKey] = shuffleMode.ordinal
        }
    }

    suspend fun setRepeatMode(repeatMode: RepeatMode) {
        context.dataStore.edit { preferences ->
            preferences[repeatModeKey] = repeatMode.ordinal
        }
    }

    fun getSongsSortOption(): Flow<SongsSortOption> {
        return context.dataStore.data.map { preferences ->
            SongsSortOption.values()[preferences[songsSortOptionKey] ?: SongsSortOption.TITLE.ordinal]
        }
    }

    fun getPlaylistSongsSortOption(): Flow<PlaylistSongsSortOption> {
        return context.dataStore.data.map { preferences ->
            PlaylistSongsSortOption.values()[preferences[playlistSongsSortOptionKey] ?: PlaylistSongsSortOption.TITLE.ordinal]
        }
    }

    fun getArtistAlbumsSortOption(): Flow<ArtistAlbumsSortOption> {
        return context.dataStore.data.map { preferences ->
            ArtistAlbumsSortOption.values()[preferences[artistAlbumsSortOptionKey] ?: ArtistAlbumsSortOption.TITLE.ordinal]
        }
    }

    fun getFavouriteSongsSortOption(): Flow<FavouriteSongsSortOption> {
        return context.dataStore.data.map { preferences ->
            FavouriteSongsSortOption.values()[preferences[favouriteSongsSortOptionKey] ?: FavouriteSongsSortOption.TITLE.ordinal]
        }
    }

    fun getShuffleMode(): Flow<ShuffleMode> {
        return context.dataStore.data.map { preferences ->
            ShuffleMode.values()[preferences[shuffleModeKey] ?: ShuffleMode.OFF.ordinal]
        }
    }

    fun getRepeatMode(): Flow<RepeatMode> {
        return context.dataStore.data.map {preferences ->
            RepeatMode.values()[preferences[repeatModeKey] ?: RepeatMode.OFF.ordinal]
        }
    }
}