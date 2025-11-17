package com.example.gamezone.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gamezone.data.User
import com.example.gamezone.data.UserRepository
import com.example.gamezone.models.UsuarioErrores
import com.example.gamezone.models.UsuarioUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UsuarioViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _estado = MutableStateFlow(value = UsuarioUiState())
    val estado: StateFlow<UsuarioUiState> = _estado

    private val _errores = MutableStateFlow(UsuarioErrores())
    val errores: StateFlow<UsuarioErrores> = _errores

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _registrationResult = MutableStateFlow<String?>(null)
    val registrationResult: StateFlow<String?> = _registrationResult

    fun onNombreChange(valor: String) {
        _estado.update { it.copy(nombre = valor, errores = it.errores.copy(nombre = null)) }
    }

    fun onCorreoChange(valor: String) {
        _estado.update { it.copy(correo = valor, errores = it.errores.copy(correo = null)) }
    }

    fun onClaveChange(valor: String) {
        _estado.update { it.copy(clave = valor, errores = it.errores.copy(clave = null)) }
    }

    fun onTelefonoChange(valor: String) {
        _estado.update { it.copy(telefono = valor, errores = it.errores.copy(telefono = null)) }

    }

    fun onAceptarTerminosChange(valor: Boolean) {
        _estado.update { it.copy(aceptaTerminos = valor) }
    }

    fun validate(): Boolean {
        val s = _estado.value
        var ok = true

        val nombreErr = if (s.nombre.isBlank()) "Obligatorio" else null

        val emailErr = when {
            s.correo.isBlank() -> "Obligatorio"
            !android.util.Patterns.EMAIL_ADDRESS.matcher(s.correo).matches() -> "Correo inválido"
            else -> null
        }
        val claveErr = when {
            s.clave.isBlank() -> "Obligatorio"
            s.clave.length < 8 -> "La clave debe tener más de 8 carácteres"
            else -> null
        }

        val telefonoErr = if (s.telefono.isBlank()) "Obligatorio" else null

        val termErr = if (!s.aceptaTerminos) "Debes aceptar términos" else null

        _errores.value = UsuarioErrores(nombreErr, emailErr, claveErr, telefonoErr,termErr)
        ok = listOf(nombreErr, emailErr, claveErr, telefonoErr, termErr).all { it == null }
        return ok
    }
    fun reset() {
        _estado.value = UsuarioUiState()
        _errores.value = UsuarioErrores()
        _registrationResult.value = null
    }

    fun register() {
        if (!validate()) {
            _registrationResult.value = "Revisa los campos resaltados."
            return
        }

        _isLoading.value = true
        _registrationResult.value = null

        viewModelScope.launch {
            try {
                val state = _estado.value
                val success = userRepository.registerUser(
                    email = state.correo,
                    password = state.clave,
                    name = state.nombre,
                    phone = state.telefono
                )
                
                if (success) {
                    _registrationResult.value = "Usuario registrado exitosamente"

                } else {
                    _registrationResult.value = "El usuario ya existe"
                }
            } catch (e: Exception) {
                _registrationResult.value = "Error al registrar usuario: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    suspend fun getUserByEmail(email: String): User? {
        return userRepository.getUserByEmail(email)
    }
}