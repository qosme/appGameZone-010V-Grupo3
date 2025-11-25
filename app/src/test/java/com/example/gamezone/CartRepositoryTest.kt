package com.example.gamezone

import com.example.gamezone.data.Cart
import com.example.gamezone.data.CartDao
import com.example.gamezone.data.CartItem
import com.example.gamezone.data.CartItemWithGame
import com.example.gamezone.data.CartRepository
import com.example.gamezone.data.Game
import io.mockk.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class CartRepositoryTest {

    private lateinit var cartDao: CartDao
    private lateinit var cartRepository: CartRepository

    @BeforeEach
    fun setUp() {
        cartDao = mockk()
        cartRepository = CartRepository(cartDao)
    }

    @Test
    fun `getCartByUserId debe devolver el carro cuando existe`() = runBlocking {
        // Dado
        val userId = "usuario123"
        val expectedCart = Cart(id = "cart_usuario123", userId = userId)
        coEvery { cartDao.getCartByUserId(userId) } returns expectedCart

        // Cuando
        val cart = cartRepository.getCartByUserId(userId)

        // Entonces
        assertEquals(expectedCart, cart)
        coVerify { cartDao.getCartByUserId(userId) }
    }

    @Test
    fun `getCartByUserId debe devolver nulo cuando no haya carro`() = runBlocking {
        // Dado
        val userId = "usuario123"
        coEvery { cartDao.getCartByUserId(userId) } returns null

        // Cuando
        val cart = cartRepository.getCartByUserId(userId)

        // Entonces
        assertNull(cart)
        coVerify { cartDao.getCartByUserId(userId) }
    }

    @Test
    fun `getCartByUserId debe devolver el carro correcto`() = runBlocking {
        val userId = "usuario123"
        val cartId = "cart_$userId"
        val expectedCart = Cart(
            id = cartId,
            userId = userId,
            totalAmount = 1000.0,
            itemCount = 1,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )

        // Mockeando CartDao
        coEvery { cartDao.getCartByUserId(userId) } returns expectedCart

        // Llamar al metodo del repositorio
        val result = cartRepository.getCartByUserId(userId)

        // Validar el resultado
        assertNotNull(result)
        assertEquals(expectedCart.id, result?.id)
        assertEquals(expectedCart.userId, result?.userId)
        assertEquals(expectedCart.totalAmount, result?.totalAmount)
    }


}
