package com.joseruiz.secret_chat.repository

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.joseruiz.secret_chat.data.User
import kotlinx.coroutines.tasks.await

@SuppressLint("StaticFieldLeak")
val firestore = FirebaseFirestore.getInstance()

suspend fun register(email: String, password: String, confirmPassword: String, navController: NavController, context: Context) {
    if (email.trim().isNotEmpty() && password.trim().isNotEmpty() && confirmPassword.trim().isNotEmpty() && password == confirmPassword) {
        try {
            val user = User(email, password)
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).await()
            saveUserToFirestore(user)
            Toast.makeText(context, "Registro exitoso", Toast.LENGTH_SHORT).show()
            navController.navigate("login")

        } catch (e: Exception) {
            Log.e("ERROR_FIREBASE", e.message.toString())
            showAlert(context, e.message.toString())
        }
    } else {
        showAlert(context, "Por favor, completa todos los campos y asegúrate de que las contraseñas coincidan.")
    }
}

private suspend fun saveUserToFirestore(user: User) {
    firestore.collection("users").document(user.email).set(user).await()
}

private fun showAlert(context: Context, message: String) {
    val builder = AlertDialog.Builder(context)
    builder.setTitle("Error")
    builder.setMessage(message)
    builder.setPositiveButton("Aceptar", null)
    val dialog: AlertDialog = builder.create()
    dialog.show()
}