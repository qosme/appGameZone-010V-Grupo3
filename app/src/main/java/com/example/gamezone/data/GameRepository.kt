package com.example.gamezone.data

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GameRepository @Inject constructor(
    private val gameDao: GameDao
) {
    fun getAllGames(): Flow<List<Game>> {
        return gameDao.getAllGames()
    }

    suspend fun getGameById(gameId: String): Game? {
        return gameDao.getGameById(gameId)
    }

    fun getGamesByCategory(category: String): Flow<List<Game>> {
        return gameDao.getGamesByCategory(category)
    }

    fun searchGames(searchQuery: String): Flow<List<Game>> {
        return gameDao.searchGames("%$searchQuery%")
    }

    suspend fun insertGame(game: Game) {
        gameDao.insertGame(game)
    }

    suspend fun insertGames(games: List<Game>) {
        gameDao.insertGames(games)
    }

    suspend fun updateGame(game: Game) {
        gameDao.updateGame(game)
    }

    suspend fun deleteGame(game: Game) {
        gameDao.deleteGame(game)
    }

    suspend fun deleteGameById(gameId: String) {
        gameDao.deleteGameById(gameId)
    }
}
