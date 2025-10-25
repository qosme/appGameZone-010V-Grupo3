package com.example.gamezone.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {
    @Query("SELECT * FROM orders WHERE userId = :userId ORDER BY createdAt DESC")
    fun getOrdersByUserId(userId: String): Flow<List<Order>>

    @Query("SELECT * FROM orders WHERE id = :orderId")
    suspend fun getOrderById(orderId: String): Order?

    @Query("SELECT oi.*, g.name as gameName, g.imageResId FROM order_items oi " +
           "JOIN games g ON oi.gameId = g.id WHERE oi.orderId = :orderId")
    fun getOrderItemsWithGameInfo(orderId: String): Flow<List<OrderItemWithGame>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: Order)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrderItem(orderItem: OrderItem)

    @Update
    suspend fun updateOrder(order: Order)

    @Query("UPDATE orders SET status = :status WHERE id = :orderId")
    suspend fun updateOrderStatus(orderId: String, status: String)

    @Delete
    suspend fun deleteOrder(order: Order)
}

data class OrderItemWithGame(
    val id: String,
    val orderId: String,
    val gameId: String,
    val quantity: Int,
    val price: Double,
    val gameName: String,
    val imageResId: Int
)
