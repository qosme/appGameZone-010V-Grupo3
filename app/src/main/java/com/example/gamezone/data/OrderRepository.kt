package com.example.gamezone.data

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrderRepository @Inject constructor(
    private val orderDao: OrderDao
) {
    fun getOrdersByUserId(userId: String): Flow<List<Order>> {
        return orderDao.getOrdersByUserId(userId)
    }

    suspend fun getOrderById(orderId: String): Order? {
        return orderDao.getOrderById(orderId)
    }

    fun getOrderItemsWithGameInfo(orderId: String): Flow<List<OrderItemWithGame>> {
        return orderDao.getOrderItemsWithGameInfo(orderId)
    }

    suspend fun createOrder(
        userId: String,
        totalAmount: Double,
        shippingAddress: String,
        paymentMethod: String,
        orderItems: List<CartItemWithGame>
    ): String? {
        return try {
            val orderId = "order_${userId}_${System.currentTimeMillis()}"
            val order = Order(
                id = orderId,
                userId = userId,
                totalAmount = totalAmount,
                shippingAddress = shippingAddress,
                paymentMethod = paymentMethod
            )
            orderDao.insertOrder(order)

            // Create order items
            orderItems.forEach { cartItem ->
                val orderItem = OrderItem(
                    id = "order_item_${orderId}_${cartItem.gameId}_${System.currentTimeMillis()}",
                    orderId = orderId,
                    gameId = cartItem.gameId,
                    quantity = cartItem.quantity,
                    price = cartItem.price,
                    gameName = cartItem.gameName
                )
                orderDao.insertOrderItem(orderItem)
            }

            orderId
        } catch (e: Exception) {
            null
        }
    }

    suspend fun updateOrderStatus(orderId: String, status: String): Boolean {
        return try {
            orderDao.updateOrderStatus(orderId, status)
            true
        } catch (e: Exception) {
            false
        }
    }
}
