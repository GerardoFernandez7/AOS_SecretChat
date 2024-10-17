package com.joseruiz.secret_chat.repository

import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.joseruiz.secret_chat.data.User
import kotlinx.coroutines.tasks.await

suspend fun getAllUsers(): List<User> {
    val db: FirebaseFirestore = Firebase.firestore
    return try {
        val snapshot = db.collection("users").get().await()
        snapshot.documents.map { document ->
            User(
                email = document.getString("email") ?: "",
                password = document.getString("password") ?: ""
            )
        }
    } catch (e: Exception) {
        e.printStackTrace()
        emptyList()
    }
}