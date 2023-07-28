package com.techdroidcentre.database.di

import android.content.Context
import androidx.room.Room
import com.techdroidcentre.database.MusicDatabase
import com.techdroidcentre.database.dao.PlaylistsDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideMusicDatabase(
        @ApplicationContext context: Context,
    ): MusicDatabase = Room.databaseBuilder(
        context,
        MusicDatabase::class.java,
        "music-database"
    ).build()

    @Provides
    @Singleton
    fun providePlaylistDao(database: MusicDatabase): PlaylistsDao = database.playlistsDao
}