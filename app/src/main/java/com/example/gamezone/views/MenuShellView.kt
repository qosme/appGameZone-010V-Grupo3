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
fun MenuShellView(navController: NavController, userEmail: String,
                  cartViewModel: CartViewModel = hiltViewModel()
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val innerNavController = rememberNavController()

    LaunchedEffect(userEmail) {
        if (userEmail.isNotEmpty()){
            cartViewModel.setUser(userEmail)
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text(
                    text = "Menú",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(16.dp)
                )
                // --- Menu items ---
                NavigationDrawerItem(
                    label = { Text("Página Principal") },
                    selected = currentInnerRoute(innerNavController) == Route.Bienvenida.route,
                    onClick = {
                        innerNavController.navigate(Route.Bienvenida.route) { launchSingleTop = true }
                        scope.launch { drawerState.close() }
                    }
                )

                NavigationDrawerItem(
                    label = { Text("Juegos") },
                    selected = currentInnerRoute(innerNavController) == Route.Juegos.route,
                    onClick = {
                        innerNavController.navigate(Route.Juegos.route) { launchSingleTop = true }
                        scope.launch { drawerState.close() }
                    }
                )

                NavigationDrawerItem(
                    label = { Text("Mi Perfil") },
                    selected = currentInnerRoute(innerNavController) == Route.Profile.route,
                    onClick = {
                        innerNavController.navigate(Route.Profile.route) { launchSingleTop = true }
                        scope.launch { drawerState.close() }
                    }
                )

                NavigationDrawerItem(
                    label = {Text("Mi Carrito de Compras")},
                    selected = currentInnerRoute(innerNavController) == Route.Cart.route,
                    onClick = {
                        innerNavController.navigate(Route.Cart.route) {launchSingleTop = true }
                        scope.launch { drawerState.close() }
                    }
                )

                //NavigationDrawerItem(
                //    label = { Text("Login") },
                //    selected = false,
                //    onClick = {
                //        navController.navigate(Route.Login.route) {
                //            popUpTo(Route.MenuShell.route) { inclusive = true }
                //            launchSingleTop = true
                //        }
                //        scope.launch { drawerState.close() }
                //    }
                //)
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
            // --- INTERNAL NAVHOST ---
            NavHost(
                navController = innerNavController,
                startDestination = Route.Bienvenida.route,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(Route.Bienvenida.route) { BienvenidaView() }
                composable(Route.Juegos.route) { JuegosView(navController = innerNavController) }

                // PROFILE SCREEN
                composable(Route.Profile.route) {
                    ProfileView(
                        userEmail = userEmail,
                        onBack = { innerNavController.navigateUp() }
                    )
                }

                // ADDITIONAL SCREENS
                composable(Route.Option3.route) { RegistroView() }
                composable(Route.Option4.route) { Option4View() }
                composable(Route.Option5.route) { Option5CameraView() }

                composable(Route.AdminProfile.route) {
                    AdminProfileView(
                        userEmail = userEmail,
                        onBack = { innerNavController.navigateUp() },
                        onNavigateToAddGame = { innerNavController.navigate(Route.AddGame.route) }
                    )
                }

                composable(Route.AddGame.route) {
                    AddGameView(
                        onBack = { innerNavController.navigateUp() },
                        onGameAdded = { innerNavController.navigateUp() }
                    )
                }

                // CART / CHECKOUT
                composable(Route.Cart.route) {
                    CartView(
                        userEmail = userEmail,
                        onNavigateToCheckout = { innerNavController.navigate(Route.Checkout.route) }
                    )
                }
                composable(Route.Checkout.route) {
                    CheckoutView(onOrderComplete = { innerNavController.navigate(Route.Bienvenida.route) })
                }

                // Game detail with argument
                composable(Route.JuegosDetalle.route) { backStack ->
                    val id = backStack.arguments?.getString("id") ?: "sin-id"
                    GameDetailView(
                        gameId = id,
                        userEmail = userEmail,
                        onBack = { innerNavController.navigateUp() },
                        onNavigateToCart = { innerNavController.navigate(Route.Cart.route) }
                    )
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
