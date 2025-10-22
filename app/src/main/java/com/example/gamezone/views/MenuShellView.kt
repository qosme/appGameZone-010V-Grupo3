package com.example.gamezone.views

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import kotlinx.coroutines.launch
import com.example.gamezone.navigation.Route

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuShellView() {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val innerNavController = rememberNavController()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text(
                    text = "Menú",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(16.dp)
                )
                NavigationDrawerItem(
                    label = { Text("Página Principal") },
                    selected = currentInnerRoute(innerNavController) == Route.Bienvenida.route,
                    onClick = {
                        innerNavController.navigate(Route.Bienvenida.route) {
                            popUpTo(Route.Bienvenida.route) { inclusive = false }
                            launchSingleTop = true
                        }
                        scope.launch { drawerState.close() }
                    }
                )
                NavigationDrawerItem(
                    label = { Text("Juegos") },
                    selected = currentInnerRoute(innerNavController) == Route.Juegos.route,
                    onClick = {
                        innerNavController.navigate(Route.Juegos.route) {
                            popUpTo(Route.Bienvenida.route) { inclusive = false }
                            launchSingleTop = true
                        }
                        scope.launch { drawerState.close() }
                    }
                )
                NavigationDrawerItem(
                    label = { Text("Registro") },
                    selected = currentInnerRoute(innerNavController) == Route.Option3.route,
                    onClick = {
                        innerNavController.navigate(Route.Option3.route) {
                            popUpTo(Route.Bienvenida.route) { inclusive = false }
                            launchSingleTop = true
                        }
                        scope.launch { drawerState.close() }
                    }
                )

                NavigationDrawerItem(
                    label = { Text("Persistencia y Animaciones") },
                    selected = currentInnerRoute(innerNavController) == Route.Option4.route,
                    onClick = {
                        innerNavController.navigate(Route.Option4.route) {
                            popUpTo(Route.Bienvenida.route) { inclusive = false }
                            launchSingleTop = true
                        }
                        scope.launch { drawerState.close() }
                    }
                )

                NavigationDrawerItem(
                    label = { Text("Cámara") },
                    selected = currentInnerRoute(innerNavController) == Route.Option5.route,
                    onClick = {
                        innerNavController.navigate(Route.Option5.route) {
                            popUpTo(Route.Bienvenida.route) { inclusive = false }
                            launchSingleTop = true
                        }
                        scope.launch { drawerState.close() }
                    }
                )

                NavigationDrawerItem(
                    label = { Text("Login") },
                    selected = currentInnerRoute(innerNavController) == Route.Login.route,
                    onClick = {
                        innerNavController.navigate(Route.Login.route) {
                            popUpTo(Route.Bienvenida.route) { inclusive = false }
                            launchSingleTop = true
                        }
                        scope.launch { drawerState.close() }
                    }
                )

            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("GAMEZONE") },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch {
                                if (drawerState.isClosed) drawerState.open() else drawerState.close()
                            }
                        }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menú")
                        }
                    }
                )
            }
        ) { innerPadding ->
            // NavHost interno para las opciones del menú
            NavHost(
                navController = innerNavController,
                startDestination = Route.Bienvenida.route,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(Route.Bienvenida.route) { BienvenidaView() }
                composable(Route.Juegos.route) { JuegosView(navController = innerNavController) } // <--recibe nav
                composable(Route.Option3.route) { RegistroView() }
                //pantalla de detalle para la clase 2(con nav)
                composable(Route.JuegosDetalle.route) { backStack ->
                    val id = backStack.arguments?.getString("id") ?: "sin-id"
                    JuegosDetalleView(
                        id = id,
                        onBack = { innerNavController.navigateUp() }
                    )
                }
                composable(Route.Option4.route) { Option4View() }
                composable(Route.Option5.route) { Option5CameraView() }
                composable(Route.Login.route) {LoginView()}
            }
        }
    }
}

@Composable
private fun currentInnerRoute(navController: NavHostController): String? {
    val entry by navController.currentBackStackEntryAsState()
    return entry?.destination?.route
}