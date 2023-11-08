package com.techdroidcentre.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.techdroidcentre.database.model.TopAlbumEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TopAlbumsDao {
    @Query("SELECT * FROM top_albums ORDER BY total_play_count DESC")
    fun getTopAlbums(): Flow<List<TopAlbumEntity>>

    @Query("SELECT * FROM top_albums WHERE id = :albumId")
    suspend fun getTopAlbum(albumId: Long): TopAlbumEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTopAlbum(topAlbumEntity: TopAlbumEntity)

    @Query("DELETE FROM top_albums WHERE id = :albumId")
    suspend fun deleteTopAlbum(albumId: Long)
}