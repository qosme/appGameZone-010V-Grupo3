package com.example.gamezone.models

data class UsuarioUiState(
    val nombre: String = "",              // Nombre del usuario
    val correo: String = "",              // Correo electrónico
    val clave: String = "",               // Contraseña
    val telefono: String = "",           // Dirección del usuario
    val aceptaTerminos: Boolean = false,  // Confirmación de términos
    val errores: UsuarioErrores = UsuarioErrores() // Objeto que contiene los errores por campo
)