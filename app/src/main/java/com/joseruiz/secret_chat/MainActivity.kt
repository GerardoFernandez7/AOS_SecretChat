package com.joseruiz.secret_chat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.joseruiz.secret_chat.screens.ChatScreen
import com.joseruiz.secret_chat.screens.Contact
import com.joseruiz.secret_chat.screens.ContactListView
import com.joseruiz.secret_chat.screens.LoginScreen
import com.joseruiz.secret_chat.screens.Message
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
                    composable(route = "contacts") {
                        ContactListView(contacts = listOf(
                            Contact("Alice"),
                            Contact("Bob"),
                            Contact("Charlie")
                        ))
                    }
                    composable(route = "chat") {
                        ChatScreen(
                            messages = listOf(
                                Message("Hola", true),
                                Message("¿Cómo estás?", false),
                                Message("Bien, ¿y tú?", true),
                                Message("Todo bien, gracias", false)
                            )
                        )
                    }
                }
            }
        }
    }
}
