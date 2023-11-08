package com.techdroidcentre.database.di

import android.content.Context
import androidx.room.Room
import com.techdroidcentre.database.MusicDatabase
import com.techdroidcentre.database.dao.PlaylistSongsDao
import com.techdroidcentre.database.dao.PlaylistsDao
import com.techdroidcentre.database.dao.TopAlbumsDao
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
    fun providePlaylistsDao(database: MusicDatabase): PlaylistsDao = database.playlistsDao

    @Provides
    @Singleton
    fun providePlaylistSongsDao(database: MusicDatabase): PlaylistSongsDao = database.playlistSongsDao

    @Provides
    @Singleton
    fun provideTopAlbumsDao(database: MusicDatabase): TopAlbumsDao = database.topAlbumsDao
}