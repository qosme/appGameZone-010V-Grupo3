package com.example.gamezone.viewModels

import androidx.lifecycle.ViewModel
import com.example.gamezone.models.UsuarioErrores
import com.example.gamezone.models.UsuarioUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class UsuarioViewModel  : ViewModel() {

    private val _estado = MutableStateFlow(value = UsuarioUiState())
    val estado: StateFlow<UsuarioUiState> = _estado

    private val _errores = MutableStateFlow(UsuarioErrores())
    val errores: StateFlow<UsuarioErrores> = _errores

    fun onNombreChange(valor: String) {
        _estado.update { it.copy(nombre = valor, errores = it.errores.copy(nombre = null)) }
    }

    // Actualiza el campo correo
    fun onCorreoChange(valor: String) {
        _estado.update { it.copy(correo = valor, errores = it.errores.copy(correo = null)) }
    }

    // Actualiza el campo clave
    fun onClaveChange(valor: String) {
        _estado.update { it.copy(clave = valor, errores = it.errores.copy(clave = null)) }
    }

    // Actualiza el campo telefono
    fun onTelefonoChange(valor: String) {
        _estado.update { it.copy(telefono = valor, errores = it.errores.copy(telefono = null)) }

    }

    // Actualiza checkbox de aceptación
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
    }



}