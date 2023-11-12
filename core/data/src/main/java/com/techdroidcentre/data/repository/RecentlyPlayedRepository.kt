package com.techdroidcentre.data.repository

import com.techdroidcentre.model.RecentlyPlayed
import kotlinx.coroutines.flow.Flow

interface RecentlyPlayedRepository {
    fun getRecentlyPlayed(): Flow<List<RecentlyPlayed>>

    suspend fun addRecentlyPlayed(recentlyPlayed: RecentlyPlayed)
}