package com.example.gamezone.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gamezone.data.Game
import com.example.gamezone.data.GameDataInitializer
import com.example.gamezone.data.GameRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminProfileViewModel @Inject constructor(
    private val gameRepository: GameRepository,
    private val gameDataInitializer: GameDataInitializer
) : ViewModel() {

    private val _games = MutableStateFlow<List<Game>>(emptyList())
    val games: StateFlow<List<Game>> get() = _games

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    init {
        loadGames()
    }
    fun loadGames() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Initialize data if necessary
                gameDataInitializer.initializeGameData()

                // Fetch the games
                gameRepository.getAllGames()
                    .onEach { fetchedGames ->
                        _games.value = fetchedGames
                    }
                    .launchIn(viewModelScope)
            } finally {
                _isLoading.value = false
            }
        }
    }



    fun deleteGame(gameId: String) {
        viewModelScope.launch {
            try {
                gameRepository.deleteGameById(gameId)
                loadGames() // Reload the list after deleting
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
                    loadGames() // Reload the list after updating
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
                loadGames() // Reload the list after adding
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}
