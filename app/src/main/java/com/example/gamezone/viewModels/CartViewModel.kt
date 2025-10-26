package com.example.gamezone.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gamezone.data.CartItemWithGame
import com.example.gamezone.data.CartRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _cartItems = MutableStateFlow<List<CartItemWithGame>>(emptyList())
    val cartItems: StateFlow<List<CartItemWithGame>> = _cartItems

    private val _totalAmount = MutableStateFlow(0.0)
    val totalAmount: StateFlow<Double> = _totalAmount

    private val _itemCount = MutableStateFlow(0)
    val itemCount: StateFlow<Int> = _itemCount

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _addToCartResult = MutableStateFlow<String?>(null)
    val addToCartResult: StateFlow<String?> = _addToCartResult

    private var currentUserEmail: String? = null
    private var currentCartId: String? = null

    // Set the currently logged-in user
    fun setUser(userEmail: String) {
        currentUserEmail = userEmail
        viewModelScope.launch {
            _isLoading.value = true
            val cart = cartRepository.getOrCreateCartForUser(userEmail)
            currentCartId = cart.id
            loadCartItems(cart.id)
            _isLoading.value = false
        }
    }

    private fun loadCartItems(cartId: String) {
        viewModelScope.launch {
            cartRepository.getCartItemsWithGameInfo(cartId).collect { items ->
                _cartItems.value = items
                _totalAmount.value = items.sumOf { it.price * it.quantity }
                _itemCount.value = items.sumOf { it.quantity }
            }
        }
    }

    fun addToCart(gameId: String, price: Double) {
        val userEmail = currentUserEmail ?: return
        val cartId = currentCartId ?: run {
            viewModelScope.launch {
                val cart = cartRepository.getOrCreateCartForUser(userEmail)
                currentCartId = cart.id
                performAddToCart(cart.id, gameId, price)
            }
            return
        }

        viewModelScope.launch {
            performAddToCart(cartId, gameId, price)
        }
    }


    private suspend fun performAddToCart(cartId: String, gameId: String, price: Double) {
        val success = cartRepository.addToCart(cartId, gameId, price)
        _addToCartResult.value = if (success) "Juego agregado al carrito" else "Error al agregar al carrito"
        loadCartItems(cartId) // refresh cart after adding
    }

    fun clearAddToCartResult() {
        _addToCartResult.value = null
    }

    fun updateQuantity(cartItemId: String, quantity: Int) {
        if (quantity <= 0) return
        viewModelScope.launch {
            cartRepository.updateCartItemQuantity(cartItemId, quantity)
            // refresh the cart after updating
            currentCartId?.let { loadCartItems(it) }
        }
    }

    fun removeFromCart(cartItemId: String) {
        viewModelScope.launch {
            cartRepository.removeFromCart(cartItemId)
            // refresh the cart after removing
            currentCartId?.let { loadCartItems(it) }
        }
    }

}
