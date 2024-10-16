package com.joseruiz.secret_chat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.joseruiz.secret_chat.screens.LoginScreen
import com.joseruiz.secret_chat.screens.RegisterScreen
import com.joseruiz.secret_chat.ui.theme.Secret_ChatTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Secret_ChatTheme {
                // Manejo de las rutas de la aplicación
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "login") {
                    composable(route = "login") {
                        LoginScreen(navController = navController)
                    }
                    composable(route = "register") {
                        RegisterScreen(navController = navController)
                    }
                }
            }
        }
    }
}
