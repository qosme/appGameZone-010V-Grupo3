package com.example.gamezone

import com.example.gamezone.data.User
import com.example.gamezone.data.UserRepository
import com.example.gamezone.viewModels.UserListViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.resetMain

class UserListViewModelTest {

    private lateinit var userListViewModel: UserListViewModel
    private val userRepository: UserRepository = mockk()
    private val testDispatcher = StandardTestDispatcher()

    // Esta es la MutableStateFlow para simular las actualizaciones en el test
    private val mockUsersFlow = MutableStateFlow<List<User>>(emptyList())

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        // Mockear getAllUsers() para devovler el mock MutableStateFlow
        coEvery { userRepository.getAllUsers() } returns mockUsersFlow

        // Mockear fetchAndSaveApiUsers()
        coEvery { userRepository.fetchAndSaveApiUsers() } returns Unit

        // Inicializar el viewmodel
        userListViewModel = UserListViewModel(userRepository)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `debe buscar usuarios`() = runTest {
        // Cuando el viewmoedl esta inicializado
        userListViewModel

        // Entonces verificar que fetchAndSaveApiUsers se llamo en la coroutine
        coVerify { userRepository.fetchAndSaveApiUsers() }
    }

}
