package com.techdroidcentre.data.repository

import com.techdroidcentre.data.mapper.toEntity
import com.techdroidcentre.data.mapper.toModel
import com.techdroidcentre.database.dao.RecentlyPlayedDao
import com.techdroidcentre.model.RecentlyPlayed
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DefaultRecentlyPlayedRepository @Inject constructor(
    private val recentlyPlayedDao: RecentlyPlayedDao
): RecentlyPlayedRepository {
    override fun getRecentlyPlayed(): Flow<List<RecentlyPlayed>> {
        return recentlyPlayedDao.getRecentlyPlayed().map { entities ->
            entities.map { it.toModel() }
        }
    }

    override suspend fun addRecentlyPlayed(recentlyPlayed: RecentlyPlayed) {
        recentlyPlayedDao.addRecentlyPlayed(recentlyPlayed.toEntity())
    }
}