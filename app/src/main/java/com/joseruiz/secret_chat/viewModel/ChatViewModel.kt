package com.joseruiz.secret_chat.viewModel

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class ChatViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    fun sendMessage(chatId: String, message: String, encryptionKey: String) {
        val encryptedMessage = encryptMessage(message, encryptionKey)
        db.collection("chats").document(chatId).update("messages", FieldValue.arrayUnion(encryptedMessage))
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
