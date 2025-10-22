package com.example.gamezone.viewModels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.room.util.copy
import com.example.gamezone.models.LoginErrores
import com.example.gamezone.models.LoginUsuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class LoginViewModel : ViewModel() {
    private val _estado = MutableStateFlow(value = LoginUsuario())
    val estado: StateFlow<LoginUsuario> = _estado

    private val _errores = MutableStateFlow(LoginErrores())
    val errores: StateFlow<LoginErrores> = _errores

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
    }

}

