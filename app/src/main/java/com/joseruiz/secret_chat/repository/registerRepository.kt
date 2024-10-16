package com.joseruiz.secret_chat.repository

import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth

fun register(email: String, password: String, confirmPassword: String, navController: NavController, context: Context) {
    if (email.trim().isNotEmpty() && password.trim().isNotEmpty() && confirmPassword.trim().isNotEmpty() && password == confirmPassword) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful)
                    Toast.makeText(context, "Registro exitoso", Toast.LENGTH_SHORT).show()
                    navController.navigate("login")
            }
            .addOnFailureListener { exception ->
                // Captura el error de Firebase y muestra un mensaje de alerta con el error específico
                Log.e("ERROR_FIREBASE", exception.message.toString())
                showAlert(context, exception.message.toString())
            }
    } else {
        showAlert(context, "Por favor, completa todos los campos y asegúrate de que las contraseñas coincidan.")
    }
}

private fun showAlert(context: Context, message: String) {
    val builder = AlertDialog.Builder(context)
    builder.setTitle("Error")
    builder.setMessage(message)
    builder.setPositiveButton("Aceptar", null)
    val dialog: AlertDialog = builder.create()
    dialog.show()  // Mostrar el diálogo
}