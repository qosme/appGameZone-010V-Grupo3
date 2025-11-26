package com.example.gamezone.data

import android.util.Log
import kotlinx.coroutines.flow.Flow
import org.mindrot.jbcrypt.BCrypt
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val userDao: UserDao,
    private val api: RestDataSource
) {
    suspend fun getUserByCredentials(email: String, password: String): User? {
        return userDao.getUserByCredentials(email, password)
    }

    suspend fun getUserByEmail(email: String): User? {
        val normalizedEmail = email.trim().lowercase(Locale.getDefault())
        return userDao.getUserByEmail(normalizedEmail)
    }

    fun getAllUsers(): Flow<List<User>> {
        return userDao.getAllUsers()
    }

    suspend fun insertUser(user: User) {
        userDao.insertUser(user)
    }

    suspend fun updateUser(user: User) {
        userDao.updateUser(user)
    }

    suspend fun deleteUser(user: User) {
        userDao.deleteUser(user)
    }

    suspend fun deleteUserByEmail(email: String) {
        userDao.deleteUserByEmail(email)
    }

    //suspend fun validateLogin(email: String, password: String): Boolean {
    //    val user = getUserByCredentials(email, password)
    //    return user != null
    //}

    suspend fun getUserCount() = userDao.getUserCount()


    suspend fun validateLogin(email: String, password: String): Boolean {
        // Encontrar usuario por email
        val user = getUserByEmail(email) ?: return false

        // Verificar que la clave hasheada sea igual
        return BCrypt.checkpw(password, user.password)
    }

    suspend fun registerUser(email: String, password: String, name: String, phone: String = ""): Boolean {
        val normalizedEmail = email.trim().lowercase(Locale.getDefault())
        val existingUser = getUserByEmail(normalizedEmail)
        if (existingUser != null) return false
        //hashear clave antes de guardarla
        val hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt())
        val userCount = getUserCount()
        val isAdmin = userCount == 0
        val newUser = User(
            email = normalizedEmail,
            password = hashedPassword,
            name = name,
            phone = phone,
            isAdmin = isAdmin
        )
        insertUser(newUser)
        return true
    }

    suspend fun updateUserProfile(email: String, name: String, phone: String, bio: String): Boolean {
        return try {
            userDao.updateUserProfile(email, name, phone, bio, System.currentTimeMillis())
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun updateUserProfilePicture(email: String, profilePictureUri: String?): Boolean {
        return try {
            userDao.updateUserProfilePicture(email, profilePictureUri, System.currentTimeMillis())
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun makeUserAdmin(email: String): Boolean {
        return try {
            userDao.updateUserAdminStatus(email, true, System.currentTimeMillis())
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun removeUserAdmin(email: String): Boolean {
        return try {
            userDao.updateUserAdminStatus(email, false, System.currentTimeMillis())
            true
        } catch (e: Exception) {
            false
        }
    }

    fun getAllAdmins(): Flow<List<User>> {
        return userDao.getAllAdmins()
    }

    suspend fun fetchAndSaveApiUsers() {
        try {

            val apiResponse = api.getUsers() //obtener usuarios de la api

            // mapeo de usuarios api a la base de datos existente
            val usersToSave = apiResponse.results.map { apiUser ->
                User(
                    email = apiUser.email,
                    password = BCrypt.hashpw(apiUser.login.password, BCrypt.gensalt()),
                    name = "${apiUser.name.first} ${apiUser.name.last}",
                    phone = apiUser.phone
                )
            }

            // insertar usuarios api en room
            usersToSave.forEach { insertUser(it) }
        } catch (e: Exception) {
            Log.e("UserRepository", "Error al hacer fetching de usuario API", e)
        }
    }

}
