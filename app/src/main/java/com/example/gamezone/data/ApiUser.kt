package com.example.gamezone.data

data class ApiUser(
    val name: Name,
    val email: String,
    val login: Login,
    val phone: String
)

data class Name(val first: String, val last: String)
data class Login(val password: String)