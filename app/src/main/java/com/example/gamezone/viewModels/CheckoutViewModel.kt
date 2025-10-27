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
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
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

    //fun loadCartItems(userEmail: String) {
    //    _isLoading.value = true
    //    viewModelScope.launch {
    //        try {
    //            val cart = cartRepository.getCartByUserId(userEmail)
    //            if (cart != null) {
    //                cartRepository.getCartItemsWithGameInfo(cart.id).collect { items ->
    //                    _cartItems.value = items
    //                    _totalAmount.value = items.sumOf { it.price * it.quantity }
    //                    Log.d("CheckoutViewModel", "Loaded cart items: $items")
    //                }
    //            }
    //        } catch (e: Exception) {
    //            _orderResult.value = "Error al cargar el carrito: ${e.message}"
    //        } finally {
    //            _isLoading.value = false
    //        }
    //    }
    //}

    fun loadCartItems(userEmail: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                Log.d("CheckoutViewModel", "Fetching cart for user: $userEmail")

                // Fetch the cart for the user
                val cart = cartRepository.getCartByUserId(userEmail)
                if (cart != null) {
                    // Fetch cart items for the retrieved cart
                    cartRepository.getCartItemsWithGameInfo(cart.id)
                        .catch { e ->
                            Log.e("CheckoutViewModel", "Flow error: ${e.message}", e)
                            _orderResult.value = "Error al cargar los productos del carrito"
                        }
                        .collectLatest { items ->
                            if (items.isNotEmpty()) {
                                _cartItems.value = items
                                _totalAmount.value = items.sumOf { it.price * it.quantity }
                                Log.d("CheckoutViewModel", "Loaded cart items: $items")
                            } else {
                                _orderResult.value = "Carrito vacío"
                            }
                        }
                } else {
                    _orderResult.value = "No se encontró el carrito para el usuario."
                }
            } catch (e: Exception) {
                // Catch any other exceptions (e.g., network or repository errors)
                Log.e("CheckoutViewModel", "Error al cargar el carrito: ${e.message}", e)
                _orderResult.value = "Error al cargar el carrito: ${e.message}"
            } finally {
                // Ensure the loading state is always reset
                Log.d("CheckoutViewModel", "Setting isLoading to false")
                _isLoading.value = false
            }
        }
    }



    //fun loadCartItems(userEmail: String) {
    //    _isLoading.value = true
    //    viewModelScope.launch {
    //        try {
    //            // Fetch the cart for the user
    //            val cart = cartRepository.getCartByUserId(userEmail)
    //            if (cart != null) {
    //                // Fetch cart items for the retrieved cart
    //                cartRepository.getCartItemsWithGameInfo(cart.id)
    //                    .catch { e ->
    //                        Log.e("CheckoutViewModel", "Flow error: ${e.message}", e)
    //                        _orderResult.value = "Error al cargar los productos del carrito"
    //                    }
    //                    .collectLatest { items ->
    //                        if (items.isNotEmpty()) {
    //                            _cartItems.value = items
    //                            _totalAmount.value = items.sumOf { it.price * it.quantity }
    //                            Log.d("CheckoutViewModel", "Loaded cart items: $items")
    //                        } else {
    //                            _orderResult.value = "Carrito vacío"
    //                        }
    //                    }
    //            } else {
    //                _orderResult.value = "No se encontró el carrito para el usuario."
    //            }
    //        } catch (e: Exception) {
    //            // Catch any other exceptions (e.g., network or repository errors)
    //            Log.e("CheckoutViewModel", "Error al cargar el carrito: ${e.message}", e)
    //            _orderResult.value = "Error al cargar el carrito: ${e.message}"
    //        } finally {
    //            // Ensure the loading state is always reset
    //            _isLoading.value = false
    //        }
    //    }
    //}


    fun placeOrder(userEmail: String, shippingAddress: String, paymentMethod: String) {
        _isLoading.value = true
        _orderResult.value = null
        Log.d("CheckoutViewModel", "Placing order, loading state: ${_isLoading.value}")
        viewModelScope.launch {
            try {
                val cart = cartRepository.getCartByUserId(userEmail)
                val cartItems = _cartItems.value
                val totalAmount = _totalAmount.value

                if (cartItems.isEmpty()) {
                    _orderResult.value = "El carrito está vacío"
                    _isLoading.value = false
                    return@launch
                }


                val orderId = orderRepository.createOrder(
                    userId = userEmail,
                    totalAmount = totalAmount,
                    shippingAddress = shippingAddress,
                    paymentMethod = paymentMethod,
                    orderItems = cartItems
                )

                if (orderId != null) {

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
