package com.example.gamezone

import com.example.gamezone.data.ApiResponse
import com.example.gamezone.data.ApiUser
import com.example.gamezone.data.Login
import com.example.gamezone.data.Name
import com.example.gamezone.data.RestDataSource
import com.example.gamezone.data.User
import com.example.gamezone.data.UserDao
import com.example.gamezone.data.UserRepository
import io.mockk.*
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mindrot.jbcrypt.BCrypt
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.test.assertFalse

class UserRepositoryTest {

    private lateinit var userRepository: UserRepository
    private val userDao: UserDao = mockk()
    private val api: RestDataSource = mockk()

    @BeforeEach
    fun setup() {
        userRepository = UserRepository(userDao, api)
    }


    @Test
    fun `insertUser inserta usuario nuevo`() = runTest {
        val user = User(email = "test@example.com", password = "clavehasheada", name = "Test Usuario")

        coEvery { userDao.insertUser(any()) } just runs

        userRepository.insertUser(user)

        coVerify { userDao.insertUser(user) }
    }

    @Test
    fun `updateUser actualiza un usuario existente`() = runTest {
        val user = User(email = "test@example.com", password = "clavehasheada", name = "Test Usuario")

        coEvery { userDao.updateUser(any()) } just runs

        userRepository.updateUser(user)

        coVerify { userDao.updateUser(user) }
    }

    @Test
    fun `deleteUser elimina un usuario existente`() = runTest {
        val user = User(email = "test@example.com", password = "clavehasheada", name = "Test Usuario")

        coEvery { userDao.deleteUser(any()) } just runs

        userRepository.deleteUser(user)

        coVerify { userDao.deleteUser(user) }
    }

    @Test
    fun `deleteUserByEmail elimina usuario por email`() = runTest {
        val email = "test@example.com"

        coEvery { userDao.deleteUserByEmail(any()) } just runs

        userRepository.deleteUserByEmail(email)

        coVerify { userDao.deleteUserByEmail(email) }
    }


    @Test
    fun `getUserCount devuelve la cantidad de usuarios`() = runTest {
        coEvery { userDao.getUserCount() } returns 10

        val result = userRepository.getUserCount()

        assertEquals(10, result)
    }


    @Test
    fun `validateLogin devuelve true para credenciales validas`() = runTest {
        val email = "test@example.com"
        val password = "clave123"
        val user = User(email = email, password = BCrypt.hashpw(password, BCrypt.gensalt()), name = "Test Usuario")

        coEvery { userDao.getUserByEmail(any()) } returns user

        val result = userRepository.validateLogin(email, password)

        assertTrue(result)
    }

    @Test
    fun `validateLogin devuelve false para credenciales invalidas`() = runTest {
        val email = "test@example.com"
        val password = "claveequivocada"
        val user = User(email = email, password = BCrypt.hashpw("clavecorrecta", BCrypt.gensalt()), name = "Test Usuario")

        coEvery { userDao.getUserByEmail(any()) } returns user

        val result = userRepository.validateLogin(email, password)

        assertFalse(result)
    }

    @Test
    fun `registerUser registra nuevos usuarios`() = runTest {
        val email = "usuarionuevo@example.com"
        val password = "clave123"
        val name = "Nuevo Usuario"
        val phone = "123456789"

        coEvery { userDao.getUserByEmail(any()) } returns null  // Usuario no existe
        coEvery { userDao.insertUser(any()) } just runs
        coEvery { userDao.getUserCount() } returns 0  // Este usuario es administrador

        val result = userRepository.registerUser(email, password, name, phone)

        assertTrue(result)
        coVerify { userDao.insertUser(any()) }
    }

    @Test
    fun `registerUser devuelve false si el email ya existe`() = runTest {
        val email = "usuarioexistente@example.com"
        val password = "clave123"
        val name = "Usuario Existente"
        val phone = "987654321"

        val existingUser = User(email = email, password = "clavehasheada", name = "Usuario Existente")

        coEvery { userDao.getUserByEmail(any()) } returns existingUser  // email ya existe

        val result = userRepository.registerUser(email, password, name, phone)

        assertFalse(result)
    }

    @Test
    fun `updateUserProfile actualiza el perfil de usuario`() = runTest {
        val email = "test@example.com"
        val name = "Nombre Actualizado"
        val phone = "123456789"
        val bio = "Nueva Bio"

        coEvery { userDao.updateUserProfile(any(), any(), any(), any(), any()) } just runs

        val result = userRepository.updateUserProfile(email, name, phone, bio)

        assertTrue(result)
        coVerify { userDao.updateUserProfile(email, name, phone, bio, any()) }
    }

    @Test
    fun `makeUserAdmin hace que un usuario sea administradoor`() = runTest {
        val email = "test@example.com"

        coEvery { userDao.updateUserAdminStatus(any(), any(), any()) } just runs

        val result = userRepository.makeUserAdmin(email)

        assertTrue(result)
        coVerify { userDao.updateUserAdminStatus(email, true, any()) }
    }

    @Test
    fun `removeUserAdmin elimina la credencial de administrador a un usuario`() = runTest {
        val email = "test@example.com"

        coEvery { userDao.updateUserAdminStatus(any(), any(), any()) } just runs

        val result = userRepository.removeUserAdmin(email)

        assertTrue(result)
        coVerify { userDao.updateUserAdminStatus(email, false, any()) }
    }

}
