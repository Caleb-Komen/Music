package com.techdroidcentre.data.di

import android.content.ContentResolver
import android.content.Context
import com.techdroidcentre.data.repository.AlbumsRepository
import com.techdroidcentre.data.repository.ArtistsRepository
import com.techdroidcentre.data.repository.DefaultAlbumsRepository
import com.techdroidcentre.data.repository.DefaultArtistsRepository
import com.techdroidcentre.data.repository.DefaultFavouriteSongsRepository
import com.techdroidcentre.data.repository.DefaultPlaylistSongsRepository
import com.techdroidcentre.data.repository.DefaultPlaylistsRepository
import com.techdroidcentre.data.repository.DefaultRecentlyPlayedRepository
import com.techdroidcentre.data.repository.DefaultSongsRepository
import com.techdroidcentre.data.repository.DefaultTopAlbumsRepository
import com.techdroidcentre.data.repository.FavouriteSongsRepository
import com.techdroidcentre.data.repository.PlaylistSongsRepository
import com.techdroidcentre.data.repository.PlaylistsRepository
import com.techdroidcentre.data.repository.RecentlyPlayedRepository
import com.techdroidcentre.data.repository.SongsRepository
import com.techdroidcentre.data.repository.TopAlbumsRepository
import com.techdroidcentre.database.dao.FavouriteSongsDao
import com.techdroidcentre.database.dao.PlaylistSongsDao
import com.techdroidcentre.database.dao.PlaylistsDao
import com.techdroidcentre.database.dao.RecentlyPlayedDao
import com.techdroidcentre.database.dao.TopAlbumsDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DataModule {
    @Singleton
    @Provides
    fun provideContentResolver(@ApplicationContext context: Context): ContentResolver {
        return context.contentResolver
    }

    @Singleton
    @Provides
    fun provideSongsRepository(
        contentResolver: ContentResolver
    ): SongsRepository {
        return DefaultSongsRepository(contentResolver)
    }

    @Singleton
    @Provides
    fun provideAlbumsRepository(
        contentResolver: ContentResolver
    ): AlbumsRepository {
        return DefaultAlbumsRepository(contentResolver)
    }

    @Singleton
    @Provides
    fun provideArtistsRepository(
        contentResolver: ContentResolver
    ): ArtistsRepository {
        return DefaultArtistsRepository(contentResolver)
    }

    @Singleton
    @Provides
    fun providePlaylistsRepository(
        playlistsDao: PlaylistsDao,
        playlistSongsDao: PlaylistSongsDao
    ): PlaylistsRepository {
        return DefaultPlaylistsRepository(playlistsDao, playlistSongsDao)
    }

    @Singleton
    @Provides
    fun providePlaylistSongsRepository(
        playlistSongsDao: PlaylistSongsDao
    ): PlaylistSongsRepository = DefaultPlaylistSongsRepository(playlistSongsDao)

    @Singleton
    @Provides
    fun provideTopAlbumsRepository(
        topAlbumsDao: TopAlbumsDao
    ): TopAlbumsRepository = DefaultTopAlbumsRepository(topAlbumsDao)

    @Singleton
    @Provides
    fun provideRecentlyPlayedRepository(
        recentlyPlayedDao: RecentlyPlayedDao
    ): RecentlyPlayedRepository = DefaultRecentlyPlayedRepository(recentlyPlayedDao)

    @Singleton
    @Provides
    fun provideFavouriteSongsRepository(
        favouriteSongsDao: FavouriteSongsDao
    ): FavouriteSongsRepository = DefaultFavouriteSongsRepository(favouriteSongsDao)
}