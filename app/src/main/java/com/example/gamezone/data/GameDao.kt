package com.example.gamezone.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {
    @Query("SELECT * FROM games WHERE isAvailable = 1")
    fun getAllGames(): Flow<List<Game>>

    @Query("SELECT * FROM games WHERE id = :gameId")
    suspend fun getGameById(gameId: String): Game?

    @Query("SELECT * FROM games WHERE category = :category AND isAvailable = 1")
    fun getGamesByCategory(category: String): Flow<List<Game>>

    @Query("SELECT * FROM games WHERE name LIKE :searchQuery AND isAvailable = 1")
    fun searchGames(searchQuery: String): Flow<List<Game>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGame(game: Game)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGames(games: List<Game>)

    @Update
    suspend fun updateGame(game: Game)

    @Delete
    suspend fun deleteGame(game: Game)

    @Query("DELETE FROM games WHERE id = :gameId")
    suspend fun deleteGameById(gameId: String)
}
