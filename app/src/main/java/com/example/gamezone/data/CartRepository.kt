package com.example.gamezone.data

import android.util.Log
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CartRepository @Inject constructor(
    private val cartDao: CartDao
) {

    suspend fun getCartByUserId(userId: String): Cart? {
        return cartDao.getCartByUserId(userId)
    }

    fun getCartItemsWithGameInfo(cartId: String): Flow<List<CartItemWithGame>> {
        return cartDao.getCartItemsWithGameInfo(cartId)
    }

    suspend fun createCart(userId: String): Cart {
        val cartId = "cart_${userId}_${System.currentTimeMillis()}"
        val cart = Cart(
            id = cartId,
            userId = userId
        )
        cartDao.insertCart(cart)
        return cart
    }

    suspend fun addToCart(cartId: String, gameId: String, price: Double): Boolean {
        return try {
            val existingItem = cartDao.getCartItemByGame(cartId, gameId)
            if (existingItem != null) {
                val updatedItem = existingItem.copy(quantity = existingItem.quantity + 1)
                cartDao.updateCartItem(updatedItem)
            } else {
                Log.d("CartRepository", "Adding item: cartId=$cartId, gameId=$gameId, price=$price")
                val cartItem = CartItem(
                    id = "item_${cartId}_${gameId}_${System.currentTimeMillis()}",
                    cartId = cartId,
                    gameId = gameId,
                    price = price
                )

                cartDao.insertCartItem(cartItem)
                Log.d("CartRepository", "Item inserted: $cartItem")

            }
            updateCartTotals(cartId)
            true
        } catch (e: Exception) {
            Log.e("CartRepository", "Error adding item to cart", e)
            false
        }
    }

    suspend fun updateCartItemQuantity(cartItemId: String, quantity: Int): Boolean {
        return try {
            val item = cartDao.getCartItemById(cartItemId) ?: return false
            cartDao.updateCartItem(item.copy(quantity = quantity))
            updateCartTotals(item.cartId)
            true
        } catch (e: Exception) {
            Log.e("CartRepository", "Error updating quantity", e)
            false
        }
    }

    suspend fun removeFromCart(cartItemId: String): Boolean {
        return try {
            val item = cartDao.getCartItemById(cartItemId) ?: return false
            cartDao.removeCartItem(item.id)
            updateCartTotals(item.cartId)
            true
        } catch (e: Exception) {
            Log.e("CartRepository", "Error removing item", e)
            false
        }
    }

    suspend fun clearCart(cartId: String): Boolean {
        return try {
            cartDao.clearCart(cartId)
            updateCartTotals(cartId)
            true
        } catch (e: Exception) {
            Log.e("CartRepository", "Error clearing cart", e)
            false
        }
    }

    private suspend fun updateCartTotals(cartId: String) {
        val itemCount = cartDao.getCartItemCount(cartId)
        val totalAmount = cartDao.getCartTotal(cartId) ?: 0.0
        val cart = cartDao.getCartById(cartId)
        cart?.let {
            cartDao.updateCart(
                it.copy(
                    itemCount = itemCount,
                    totalAmount = totalAmount,
                    updatedAt = System.currentTimeMillis()
                )
            )
        }
    }
    suspend fun getOrCreateCartForUser(userEmail: String): Cart {
        return getCartByUserId(userEmail) ?: run {
            val newCart = Cart(
                id = "cart_$userEmail", // persistent per-user cart
                userId = userEmail,
                totalAmount = 0.0,
                itemCount = 0,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            cartDao.insertCart(newCart)
            newCart
        }
    }

}
