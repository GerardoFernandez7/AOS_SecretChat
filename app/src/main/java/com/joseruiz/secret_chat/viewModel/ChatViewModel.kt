package com.joseruiz.secret_chat.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.joseruiz.secret_chat.data.Chat
import com.joseruiz.secret_chat.data.Message

class ChatViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    fun sendMessage(chatId: String, message: String, encryptionKey: String) {
        val encryptedMessage = encryptMessage(message, encryptionKey)
        db.collection("chats").document(chatId).update("messages", FieldValue.arrayUnion(encryptedMessage))
    }

    val firestore = FirebaseFirestore.getInstance()

    fun getChatByUsers(email1: String, email2: String, onResult: (List<Message>?) -> Unit) {
        val chatCollection = firestore.collection("chats")

        // Realizar la consulta
        chatCollection
            .whereEqualTo("user1", email1)
            .whereEqualTo("user2", email2)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.isEmpty) {
                    // Si no se encuentra, intentar con el orden inverso
                    chatCollection
                        .whereEqualTo("user1", email2)
                        .whereEqualTo("user2", email1)
                        .get()
                        .addOnSuccessListener { reverseQuerySnapshot ->
                            if (!reverseQuerySnapshot.isEmpty) {
                                val chat = reverseQuerySnapshot.documents.first().toObject(Chat::class.java)
                                onResult(chat?.messages) // Retornar la lista de mensajes
                            } else {
                                onResult(null) // No se encontró ningún chat
                            }
                        }
                } else {
                    val chat = querySnapshot.documents.first().toObject(Chat::class.java)
                    onResult(chat?.messages) // Retornar la lista de mensajes
                }
            }
            .addOnFailureListener { exception ->
                Log.e("FirestoreError", "Error getting documents: $exception")
                onResult(null) // En caso de error, devolver null
            }
    }


    private fun encryptMessage(message: String, key: String): String {
        // Aquí puedes implementar el Método del Emperador para encriptar el mensaje
        return message // Cambia esto por el mensaje encriptado
    }

    fun decryptMessage(encryptedMessage: String, key: String): String {
        // Implementa la lógica de desencriptación aquí
        return encryptedMessage // Cambia esto por el mensaje desencriptado
    }
}
