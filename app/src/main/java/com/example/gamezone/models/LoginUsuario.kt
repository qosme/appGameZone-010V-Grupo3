package com.example.gamezone.models

data class LoginUsuario(
    val correo: String = "",              // Correo electrónico
    val clave: String = "",               // Contraseña
    val errores: LoginErrores = LoginErrores()
)