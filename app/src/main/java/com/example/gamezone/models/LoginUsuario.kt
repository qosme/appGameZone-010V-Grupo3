package com.example.gamezone.models

data class LoginUsuario(
    val correo: String = "",
    val clave: String = "",
    val errores: LoginErrores = LoginErrores()
)