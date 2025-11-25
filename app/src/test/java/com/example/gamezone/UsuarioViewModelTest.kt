package com.example.gamezone

import com.example.gamezone.data.User
import com.example.gamezone.data.UserRepository
import com.example.gamezone.viewModels.UsuarioViewModel
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.mockk.coEvery
import io.mockk.coVerify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach

class UsuarioViewModelTest {

    private lateinit var usuarioViewModel: UsuarioViewModel
    private val userRepository: UserRepository = mockk()

    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)



    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        usuarioViewModel = UsuarioViewModel(userRepository)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `onNombreChange debe actualizar estado con nuevo nombre`() {
        val newName = "Juan Perez"
        usuarioViewModel.onNombreChange(newName)

        // Asegurar que nombre se haya actualizado en estado
        val estado = usuarioViewModel.estado.value
        assertEquals(newName, estado.nombre)
    }

}
