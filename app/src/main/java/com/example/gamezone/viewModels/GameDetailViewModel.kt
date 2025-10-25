package com.example.gamezone.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gamezone.data.Game
import com.example.gamezone.data.GameRepository
import com.example.gamezone.data.CartRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameDetailViewModel @Inject constructor(
    private val gameRepository: GameRepository,
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _game = MutableStateFlow<Game?>(null)
    val game: StateFlow<Game?> = _game

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _addToCartResult = MutableStateFlow<String?>(null)
    val addToCartResult: StateFlow<String?> = _addToCartResult

    fun loadGameDetails(gameId: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val game = gameRepository.getGameById(gameId)
                _game.value = game
            } catch (e: Exception) {
                _addToCartResult.value = "Error al cargar el juego: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addToCart() {
        val currentGame = _game.value ?: return
        _isLoading.value = true

        viewModelScope.launch {
            try {
                val userId = "current_user@example.com" // Replace with real user ID
                val cart = cartRepository.getOrCreateCartForUser(userId)
                val success = cartRepository.addToCart(cart.id, currentGame.id, currentGame.price)

                _addToCartResult.value = if (success) {
                    "${currentGame.name} agregado al carrito"
                } else {
                    "Error al agregar al carrito"
                }
            } catch (e: Exception) {
                _addToCartResult.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
