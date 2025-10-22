package com.example.gamezone

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.gamezone.navigation.Route
import com.example.gamezone.views.LoginView
import com.example.gamezone.views.MenuShellView
import com.example.gamezone.views.WelcomeView

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Surface(color = MaterialTheme.colorScheme.background) {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = Route.Welcome.route
                ) {
                    composable(Route.Welcome.route) {
                        WelcomeView(
                            onStartClick = { navController.navigate(Route.MenuShell.route) }
                        )
                    }
                    // MenuShell incluye su propio NavHost interno para Option1/2/3
                    composable(Route.MenuShell.route) {
                        MenuShellView()
                    }
                }
            }
        }
    }
}