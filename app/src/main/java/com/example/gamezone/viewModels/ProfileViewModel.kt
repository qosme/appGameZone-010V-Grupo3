package com.example.gamezone.viewModels
import kotlinx.coroutines.flow.first
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gamezone.data.User
import com.example.gamezone.data.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _updateResult = MutableStateFlow<String?>(null)
    val updateResult: StateFlow<String?> = _updateResult

    fun loadUserProfile(email: String) {
        val normalizedEmail = email.trim().lowercase(Locale.getDefault())
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val user = userRepository.getUserByEmail(normalizedEmail)
                Log.d("ProfileViewModel", "User fetched: $user")
                _currentUser.value = user
            } catch (e: Exception) {
                _updateResult.value = "Error al cargar perfil: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }





    fun updateProfile(email: String, name: String, phone: String, bio: String) {
        _isLoading.value = true
        _updateResult.value = null

        viewModelScope.launch {
            try {
                val success = userRepository.updateUserProfile(email, name, phone, bio)
                if (success) {
                    _updateResult.value = "Perfil actualizado exitosamente"
                    loadUserProfile(email) // Reload the profile
                } else {
                    _updateResult.value = "Error al actualizar perfil"
                }
            } catch (e: Exception) {
                _updateResult.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateProfilePicture(email: String, profilePictureUri: String?) {
        _isLoading.value = true
        _updateResult.value = null

        viewModelScope.launch {
            try {
                val success = userRepository.updateUserProfilePicture(email, profilePictureUri)
                if (success) {
                    _updateResult.value = "Foto de perfil actualizada"
                    loadUserProfile(email) // Reload the profile
                } else {
                    _updateResult.value = "Error al actualizar foto de perfil"
                }
            } catch (e: Exception) {
                _updateResult.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearUpdateResult() {
        _updateResult.value = null
    }
}
