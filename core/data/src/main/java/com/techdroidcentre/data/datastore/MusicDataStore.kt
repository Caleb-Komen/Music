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
}