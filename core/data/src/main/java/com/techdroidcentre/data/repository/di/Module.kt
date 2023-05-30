package com.techdroidcentre.data.repository.di

import android.content.ContentResolver
import android.content.Context
import com.techdroidcentre.data.repository.DefaultSongsRepository
import com.techdroidcentre.data.repository.SongsRepository
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
object Module {
    @Singleton
    @Provides
    fun provideContentResolver(@ApplicationContext context: Context): ContentResolver {
        return context.contentResolver
    }

    @Singleton
    @Provides
    fun provideSongsRepository(
        @ApplicationContext context: Context,
        contentResolver: ContentResolver
    ): SongsRepository {
        return DefaultSongsRepository(contentResolver)
    }
}