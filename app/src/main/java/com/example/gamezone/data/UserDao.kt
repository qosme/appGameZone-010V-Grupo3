package com.example.gamezone.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE email = :email AND password = :password")
    suspend fun getUserByCredentials(email: String, password: String): User?

    @Query("SELECT * FROM users WHERE email = :email")
    suspend fun getUserByEmail(email: String): User?

    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<User>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Update
    suspend fun updateUser(user: User)

    @Delete
    suspend fun deleteUser(user: User)

    @Query("DELETE FROM users WHERE email = :email")
    suspend fun deleteUserByEmail(email: String)

    @Query("UPDATE users SET name = :name, phone = :phone, bio = :bio, updatedAt = :updatedAt WHERE email = :email")
    suspend fun updateUserProfile(email: String, name: String, phone: String, bio: String, updatedAt: Long)

    @Query("UPDATE users SET profilePictureUri = :profilePictureUri, updatedAt = :updatedAt WHERE email = :email")
    suspend fun updateUserProfilePicture(email: String, profilePictureUri: String?, updatedAt: Long)

    @Query("SELECT * FROM users WHERE isAdmin = 1")
    fun getAllAdmins(): Flow<List<User>>

    @Query("UPDATE users SET isAdmin = :isAdmin, updatedAt = :updatedAt WHERE email = :email")
    suspend fun updateUserAdminStatus(email: String, isAdmin: Boolean, updatedAt: Long)

    @Query("SELECT COUNT(*) FROM users")
    suspend fun getUserCount(): Int
}
