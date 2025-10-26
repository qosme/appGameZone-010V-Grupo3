package com.example.gamezone.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gamezone.data.CartItemWithGame
import com.example.gamezone.data.CartRepository
import com.example.gamezone.data.OrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val cartRepository: CartRepository,
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val _cartItems = MutableStateFlow<List<CartItemWithGame>>(emptyList())
    val cartItems: StateFlow<List<CartItemWithGame>> = _cartItems

    private val _totalAmount = MutableStateFlow(0.0)
    val totalAmount: StateFlow<Double> = _totalAmount

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _orderResult = MutableStateFlow<String?>(null)
    val orderResult: StateFlow<String?> = _orderResult

    fun loadCartItems(userEmail: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val cart = cartRepository.getCartByUserId(userEmail)
                if (cart != null) {
                    cartRepository.getCartItemsWithGameInfo(cart.id).collect { items ->
                        _cartItems.value = items
                        _totalAmount.value = items.sumOf { it.price * it.quantity }
                        Log.d("CheckoutViewModel", "Loaded cart items: $items")
                    }
                }
            } catch (e: Exception) {
                _orderResult.value = "Error al cargar el carrito: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Place order function
    fun placeOrder(userEmail: String, shippingAddress: String, paymentMethod: String) {
        _isLoading.value = true  // Set loading state to true while placing order
        _orderResult.value = null
        Log.d("CheckoutViewModel", "Placing order, loading state: ${_isLoading.value}")
        viewModelScope.launch {
            try {
                val cart = cartRepository.getCartByUserId(userEmail)
                val cartItems = _cartItems.value
                val totalAmount = _totalAmount.value

                if (cartItems.isEmpty()) {
                    _orderResult.value = "El carrito está vacío"
                    _isLoading.value = false  // Stop loading if cart is empty
                    return@launch
                }

                // Create order
                val orderId = orderRepository.createOrder(
                    userId = userEmail,
                    totalAmount = totalAmount,
                    shippingAddress = shippingAddress,
                    paymentMethod = paymentMethod,
                    orderItems = cartItems
                )

                if (orderId != null) {
                    // Clear cart after successful order
                    cart?.let {
                        cartRepository.clearCart(it.id)
                    }

                    _orderResult.value = "Pedido realizado exitosamente. ID: $orderId"
                } else {
                    _orderResult.value = "Error al procesar el pedido"
                }
            } catch (e: Exception) {
                _orderResult.value = "Error al procesar el pedido: ${e.message}"
            } finally {
                _isLoading.value = false  // Set loading state to false after order is placed
            }
        }
    }

}
