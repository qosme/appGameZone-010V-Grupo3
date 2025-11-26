package com.example.gamezone

import com.example.gamezone.data.CartItemWithGame
import com.example.gamezone.data.Order
import com.example.gamezone.data.OrderDao
import com.example.gamezone.data.OrderRepository
import io.mockk.*
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.test.assertFalse

class OrderRepositoryTest {

    private lateinit var repository: OrderRepository
    private val orderDao: OrderDao = mockk(relaxed = true)

    @BeforeEach
    fun setup() {
        repository = OrderRepository(orderDao)
    }

    @Test
    fun `getOrdersByUserId devuelve una lista de pedidos`() = runTest {
        val userId = "user123"
        val orders = listOf(
            Order("o1", userId, 10.0, "status", "dirección", "paypal"),
            Order("o2", userId, 20.0, "status", "dirección", "paypal")
        )

        every { orderDao.getOrdersByUserId(userId) } returns flowOf(orders)

        val result = repository.getOrdersByUserId(userId)
            .toList()

        assertEquals(listOf(orders), result)
    }


    @Test
    fun `getOrderById devuelve un pedido`() = runTest {
        val order = Order("order123", "user123", 50.0, "aa", "cc", "bb")

        coEvery { orderDao.getOrderById("order123") } returns order

        val result = repository.getOrderById("order123")

        assertEquals(order, result)
    }

    @Test
    fun `getOrderById no devuelve nada cuando falta`() = runTest {
        coEvery { orderDao.getOrderById("falta") } returns null

        val result = repository.getOrderById("falta")

        assertNull(result)
    }


    @Test
    fun `createOrder inserta orden e items`() = runTest {
        val userId = "user1"
        val items = listOf(
            CartItemWithGame("order_$userId", "c1", "juego1", 1,10.0,1000L, "Juego 1", 1),
            CartItemWithGame("order_$userId", "c2", "juego2", 1,10.0,1000L, "Juego 2", 2)
        )

        coEvery { orderDao.insertOrder(any()) } just runs
        coEvery { orderDao.insertOrderItem(any()) } just runs

        val orderId = repository.createOrder(
            userId = userId,
            totalAmount = 50.0,
            shippingAddress = "Calle",
            paymentMethod = "Tarjeta",
            orderItems = items
        )

        assertNotNull(orderId)
        assertTrue(orderId.startsWith("order_$userId"))

        coVerify(exactly = 1) { orderDao.insertOrder(any()) }
        coVerify(exactly = items.size) { orderDao.insertOrderItem(any()) }
    }

    @Test
    fun `createOrder devuelve nulo cuando hay excepcion`() = runTest {
        coEvery { orderDao.insertOrder(any()) } throws RuntimeException("error")

        val result = repository.createOrder(
            "u1", 10.0, "ubic", "tarjeta", emptyList()
        )

        assertNull(result)
    }


    @Test
    fun `updateOrderStatus devuelve true cuando la acutalizacion es exitosa`() = runTest {
        coEvery { orderDao.updateOrderStatus("o1", "Enviado") } returns Unit

        val result = repository.updateOrderStatus("o1", "Enviado")

        assertTrue(result)
    }

    @Test
    fun `updateOrderStatus devuelve false cuando hay excepcion`() = runTest {
        coEvery { orderDao.updateOrderStatus(any(), any()) } throws RuntimeException("Fallo")

        val result = repository.updateOrderStatus("o55", "Enviado")

        assertFalse(result)
    }
}
