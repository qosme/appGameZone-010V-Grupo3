package com.example.gamezone.viewModels

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

    fun loadCartItems() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val userId = "current_user@example.com"
                val cart = cartRepository.getCartByUserId(userId)
                if (cart != null) {
                    cartRepository.getCartItemsWithGameInfo(cart.id).collect { items ->
                        _cartItems.value = items
                        _totalAmount.value = items.sumOf { it.price * it.quantity }
                    }
                }
            } catch (e: Exception) {
                _orderResult.value = "Error al cargar el carrito: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun placeOrder(shippingAddress: String, paymentMethod: String) {
        _isLoading.value = true
        _orderResult.value = null
        
        viewModelScope.launch {
            try {
                val userId = "current_user@example.com"
                val cartItems = _cartItems.value
                val totalAmount = _totalAmount.value
                
                if (cartItems.isEmpty()) {
                    _orderResult.value = "El carrito está vacío"
                    return@launch
                }
                
                // Create order
                val orderId = orderRepository.createOrder(
                    userId = userId,
                    totalAmount = totalAmount,
                    shippingAddress = shippingAddress,
                    paymentMethod = paymentMethod,
                    orderItems = cartItems
                )
                
                if (orderId != null) {
                    // Clear cart after successful order
                    val cart = cartRepository.getCartByUserId(userId)
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
                _isLoading.value = false
            }
        }
    }
}
