package com.example.gamezone.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {
    @Query("SELECT * FROM carts WHERE userId = :userId")
    suspend fun getCartByUserId(userId: String): Cart?

    @Query("SELECT * FROM cart_items WHERE id = :cartItemId")
    suspend fun getCartItemById(cartItemId: String): CartItem?

    @Query("SELECT * FROM cart_items WHERE cartId = :cartId")
    fun getCartItems(cartId: String): Flow<List<CartItem>>

    @Query("SELECT ci.*, g.name as gameName, g.imageResId FROM cart_items ci " +
           "JOIN games g ON ci.gameId = g.id WHERE ci.cartId = :cartId")
    fun getCartItemsWithGameInfo(cartId: String): Flow<List<CartItemWithGame>>

    @Query("SELECT COUNT(*) FROM cart_items WHERE cartId = :cartId")
    suspend fun getCartItemCount(cartId: String): Int

    @Query("SELECT SUM(price * quantity) FROM cart_items WHERE cartId = :cartId")
    suspend fun getCartTotal(cartId: String): Double?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCart(cart: Cart)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCartItem(cartItem: CartItem)

    @Update
    suspend fun updateCart(cart: Cart)

    @Update
    suspend fun updateCartItem(cartItem: CartItem)

    @Delete
    suspend fun deleteCart(cart: Cart)

    @Delete
    suspend fun deleteCartItem(cartItem: CartItem)

    @Query("DELETE FROM cart_items WHERE cartId = :cartId")
    suspend fun clearCart(cartId: String)

    @Query("DELETE FROM cart_items WHERE id = :cartItemId")
    suspend fun removeCartItem(cartItemId: String)

    @Query("SELECT * FROM cart_items WHERE cartId = :cartId AND gameId = :gameId")
    suspend fun getCartItemByGame(cartId: String, gameId: String): CartItem?

    @Query("SELECT * FROM carts WHERE id = :cartId LIMIT 1")
    suspend fun getCartById(cartId: String): Cart?
}

data class CartItemWithGame(
    val id: String,
    val cartId: String,
    val gameId: String,
    val quantity: Int,
    val price: Double,
    val addedAt: Long,
    val gameName: String,
    val imageResId: Int
)
