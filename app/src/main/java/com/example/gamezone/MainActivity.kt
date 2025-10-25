package com.example.gamezone

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.gamezone.navigation.Route
import com.example.gamezone.views.BienvenidaView
import com.example.gamezone.views.LoginView
import com.example.gamezone.views.MenuShellView
import com.example.gamezone.views.ProfileView
import com.example.gamezone.views.RegistroView
import com.example.gamezone.views.WelcomeView
import dagger.hilt.android.AndroidEntryPoint
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Surface(color = MaterialTheme.colorScheme.background) {
                val navController = rememberNavController()

                //  Remember must be inside a composable lambda
                var loggedInEmail by remember { mutableStateOf<String?>(null) }

                NavHost(
                    navController = navController,
                    startDestination = Route.Login.route
                ) {
                    composable(Route.Login.route) {
                        LoginView(
                            onLoginSuccess = { email ->
                                loggedInEmail = email
                                navController.navigate(Route.MenuShell.route) {
                                    popUpTo(Route.Login.route) { inclusive = true }
                                }
                            },
                            onNavigateToSignUp = { navController.navigate(Route.Registro.route) }
                        )
                    }

                    composable(Route.Registro.route) {
                        RegistroView(
                            onRegistrationSuccess = { email ->
                                loggedInEmail = email
                                navController.navigate(Route.MenuShell.route) {
                                    popUpTo(Route.Registro.route) { inclusive = true }
                                }
                            }//,
                            //onNavigateToSignUp = { navController.navigate(Route.Registro.route) }
                        )
                    }

                    composable(Route.MenuShell.route) {
                        MenuShellView(
                            navController = navController,
                            userEmail = loggedInEmail ?: "sin-email@example.com"
                        )
                    }
                }
            }
        }
    }
}

