package com.techdroidcentre.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.techdroidcentre.database.model.FavouriteSongEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavouriteSongsDao {
    @Query("SELECT * FROM favourite_songs")
    fun getFavouriteSongs(): Flow<List<FavouriteSongEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addFavouriteSong(favouriteSongEntity: FavouriteSongEntity)

    @Query("SELECT EXISTS(SELECT 1 FROM favourite_songs WHERE id = :songId LIMIT 1)")
    fun isFavourite(songId: Long): Flow<Boolean>

    @Query("DELETE FROM favourite_songs WHERE id = :songId")
    suspend fun deleteFavouriteSong(songId: Long)
}