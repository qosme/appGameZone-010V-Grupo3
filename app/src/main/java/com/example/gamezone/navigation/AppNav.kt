package com.example.gamezone.navigation

sealed class Route(val route: String) {
    data object Welcome : Route("welcome")
    data object MenuShell : Route("menu_shell") // contenedor con drawer
    data object Bienvenida : Route("bienvenida")
    data object Juegos : Route("juegos")
    data object Option3 : Route("option3")

    //detalle con argumento
    data object JuegosDetalle : Route("juegos/detalle/{id}") {
        fun build(id: String) = "juegos/detalle/$id"
    }

    data object Option4 : Route("option4")

    data object Option5 : Route("option5")

    data object Login : Route("Login")
}