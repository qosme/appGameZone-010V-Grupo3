package com.example.gamezone.navigation

sealed class Route(val route: String) {

    // Outer NavHost
    object Login : Route("login")
    object Registro : Route("registro")
    object MenuShell : Route("menu_shell")

    // Inner NavHost (namespaced with "menu/")
    object Bienvenida : Route("menu/bienvenida")
    object Juegos : Route("menu/juegos")
    object Cart : Route("menu/cart")
    object Checkout : Route("menu/checkout")
    object Profile : Route("menu/profile")
    object AdminProfile : Route("menu/adminprofile")
    object AddGame : Route("menu/addgame")
    object EditGame : Route("menu/editgame/{gameId}") {
        fun createRoute(gameId: String): String {
            return "menu/editgame/$gameId"
        }
    }

    // Game detail with argument
    object JuegosDetalle : Route("menu/juegos/detalle/{id}") {
        fun build(id: String) = "menu/juegos/detalle/$id"
    }
}
