package com.example.gamezone.viewModels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.util.copy
import com.example.gamezone.data.UserRepository
import com.example.gamezone.models.LoginErrores
import com.example.gamezone.models.LoginUsuario
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    private val _estado = MutableStateFlow(value = LoginUsuario())
    val estado: StateFlow<LoginUsuario> = _estado

    private val _errores = MutableStateFlow(LoginErrores())
    val errores: StateFlow<LoginErrores> = _errores

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _loginResult = MutableStateFlow<String?>(null)
    val loginResult: StateFlow<String?> = _loginResult

    fun onCorreoChange(valor: String) {
        _estado.update { it.copy(correo = valor, errores = it.errores.copy(correo = null)) }
    }

    fun onClaveChange(valor: String) {
        _estado.update { it.copy(clave = valor, errores = it.errores.copy(clave = null)) }
    }

    fun validate(): Boolean {
        val s = _estado.value
        var ok = true

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


        _errores.value = LoginErrores( emailErr, claveErr)
        ok = listOf(emailErr, claveErr).all { it == null }
        return ok
    }

    fun reset() {
        _estado.value = LoginUsuario()
        _errores.value = LoginErrores()
        _loginResult.value = null
    }

    fun login() {
        if (!validate()) {
            _loginResult.value = "Revisa los campos resaltados."
            return
        }

        _isLoading.value = true
        _loginResult.value = null

        viewModelScope.launch {
            val normalizedEmail = _estado.value.correo.trim().lowercase(Locale.getDefault())
            try {
                val isValid = userRepository.validateLogin(
                    normalizedEmail,
                    _estado.value.clave
                )

                if (isValid) {
                    _loginResult.value = "Login exitoso"
                } else {
                    _errores.value = _errores.value.copy(
                        clave = "Contraseña incorrecta"
                    )
                    _loginResult.value = "Credenciales incorrectas"
                }
            } catch (e: Exception) {
                _loginResult.value = "Error al iniciar sesión: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }



    fun register(email: String, password: String, name: String) {
        _isLoading.value = true
        _loginResult.value = null

        viewModelScope.launch {
            try {
                val success = userRepository.registerUser(email, password, name)
                if (success) {
                    _loginResult.value = "Usuario registrado exitosamente"
                } else {
                    _loginResult.value = "El usuario ya existe"
                }
            } catch (e: Exception) {
                _loginResult.value = "Error al registrar usuario: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

}

