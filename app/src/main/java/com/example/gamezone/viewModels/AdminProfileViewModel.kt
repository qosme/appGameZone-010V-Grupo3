package com.example.gamezone.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gamezone.data.Game
import com.example.gamezone.data.GameRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminProfileViewModel @Inject constructor(
    private val gameRepository: GameRepository
) : ViewModel() {

    private val _games = MutableStateFlow<List<Game>>(emptyList())
    val games: StateFlow<List<Game>> = _games

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun loadGames() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                gameRepository.getAllGames().collect { gamesList ->
                    _games.value = gamesList
                }
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteGame(gameId: String) {
        viewModelScope.launch {
            try {
                gameRepository.deleteGameById(gameId)
                loadGames() // Reload the list
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun toggleGameAvailability(gameId: String, isAvailable: Boolean) {
        viewModelScope.launch {
            try {
                val game = _games.value.find { it.id == gameId }
                game?.let {
                    val updatedGame = it.copy(isAvailable = isAvailable)
                    gameRepository.updateGame(updatedGame)
                    loadGames() // Reload the list
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun addGame(game: Game) {
        viewModelScope.launch {
            try {
                gameRepository.insertGame(game)
                loadGames() // Reload the list
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}
