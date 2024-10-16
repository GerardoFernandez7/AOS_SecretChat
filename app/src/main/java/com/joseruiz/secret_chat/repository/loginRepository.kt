package com.joseruiz.secret_chat.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.AuthResult
import kotlinx.coroutines.tasks.await

class LoginRepository {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    // Función para iniciar sesión
    suspend fun signInWithEmailAndPassword(email: String, password: String): AuthResult? {
        return try {
            // Intento de autenticación
            auth.signInWithEmailAndPassword(email, password).await()
        } catch (e: Exception) {
            e.printStackTrace()
            null // Devuelve null si ocurre algún error
        }
    }

    // Función para verificar si el usuario está autenticado
    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    // Función para cerrar sesión
    fun signOut() {
        auth.signOut()
    }
}