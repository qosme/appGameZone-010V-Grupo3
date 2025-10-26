package com.example.gamezone.views

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import kotlinx.coroutines.launch
import com.example.gamezone.navigation.Route
import com.example.gamezone.viewModels.CartViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuShellView(
    navController: NavController, // Control para logout
    userEmail: String,
    cartViewModel: CartViewModel = hiltViewModel() //menushell
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val innerNavController = rememberNavController()

    LaunchedEffect(userEmail) {
        if (userEmail.isNotEmpty()) {
            cartViewModel.setUser(userEmail)
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text("Menú", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(16.dp))

                fun navigateInner(route: String) {
                    innerNavController.navigate(route) {
                        launchSingleTop = true
                        popUpTo(innerNavController.graph.startDestinationId) { inclusive = false }
                    }
                    scope.launch { drawerState.close() }
                }

                NavigationDrawerItem(
                    label = { Text("Página Principal") },
                    selected = currentInnerRoute(innerNavController) == Route.Bienvenida.route,
                    onClick = { navigateInner(Route.Bienvenida.route) }
                )
                NavigationDrawerItem(
                    label = { Text("Juegos") },
                    selected = currentInnerRoute(innerNavController) == Route.Juegos.route,
                    onClick = { navigateInner(Route.Juegos.route) }
                )
                NavigationDrawerItem(
                    label = { Text("Mi Perfil") },
                    selected = currentInnerRoute(innerNavController) == Route.Profile.route,
                    onClick = { navigateInner(Route.Profile.route) }
                )
                NavigationDrawerItem(
                    label = { Text("Mi Carrito de Compras") },
                    selected = currentInnerRoute(innerNavController) == Route.Cart.route,
                    onClick = { navigateInner(Route.Cart.route) }
                )
                NavigationDrawerItem(
                    label = { Text("Administrador") },
                    selected = currentInnerRoute(innerNavController) == Route.AdminProfile.route,
                    onClick = { navigateInner(Route.AdminProfile.route) }
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
                            scope.launch { if (drawerState.isClosed) drawerState.open() else drawerState.close() }
                        }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menú")
                        }
                    }
                )
            }
        ) { innerPadding ->
            NavHost(
                navController = innerNavController,
                startDestination = Route.Bienvenida.route,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(Route.Bienvenida.route) {
                    BienvenidaView() }

                composable(Route.Juegos.route) {
                    JuegosView(innerNavController) }

                composable(Route.Profile.route) {
                    ProfileView(
                        userEmail,
                        onBack = { innerNavController.navigateUp() }) }

                composable(Route.Cart.route) { backStackEntry ->
                    val cartVm: CartViewModel = hiltViewModel(backStackEntry)
                    CartView(
                        userEmail = userEmail,
                        vm = cartVm,
                        onNavigateToCheckout = { innerNavController.navigate(Route.Checkout.route) }
                    )
                }
                composable(Route.AdminProfile.route) {
                    AdminProfileView(
                        userEmail,
                        onNavigateToAddGame = { innerNavController.navigate(Route.AddGame.route) }) }

                composable(Route.AddGame.route) {
                    AddGameView(
                        onBack = { innerNavController.navigateUp() },
                        onGameAdded = { innerNavController.navigateUp() }) }

                composable(Route.JuegosDetalle.route) { backStack ->
                    val id = backStack.arguments?.getString("id") ?: ""
                    GameDetailView(
                        gameId = id,
                        userEmail = userEmail,
                        onNavigateToCart = { innerNavController.navigate(Route.Cart.route) })
                }
            }
        }
    }
}

@Composable
private fun currentInnerRoute(navController: NavHostController): String? {
    val entry by navController.currentBackStackEntryAsState()
    return entry?.destination?.route
}
