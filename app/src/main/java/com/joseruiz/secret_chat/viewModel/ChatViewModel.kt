package com.joseruiz.secret_chat.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.joseruiz.secret_chat.data.Chat
import com.joseruiz.secret_chat.data.Message


fun buscarChatPorId(idChat: String, pin: String): List<Message> {
    val db = FirebaseFirestore.getInstance()
    var messagesChat: List<Message> = listOf()
    db.collection("chats")
        .whereEqualTo("idChat", idChat)
        .get()
        .addOnSuccessListener { querySnapshot: QuerySnapshot ->
            if (!querySnapshot.isEmpty) {
                messagesChat = createChat(idChat,true, pin)
            } else {
                messagesChat = createChat(idChat,false, pin)
            }
        }
        .addOnFailureListener { exception ->
            Log.w("Firebase", "Error buscando chat", exception)
        }
        return messagesChat
}

fun createChat(idChat: String, exists: Boolean, pin: String): List<Message>{
    val db = FirebaseFirestore.getInstance()
    if (!exists){// Si no existe el chat
        val newChat = Chat(
            idChat = idChat,
            pin = pin,
            messages = listOf()
        )
        db.collection("chats")
            .document(idChat) // Usa el idChat como ID del documento
            .set(newChat)
            .addOnSuccessListener {
                Log.d("Firebase", "Chat creado con éxito")
            }
            .addOnFailureListener { exception ->
                Log.w("Firebase", "Error al crear el chat", exception)
            }
        return newChat.messages
    }else{// Si existe el chat entonces se obtiene la data
        var messagesChat: List<Message> = listOf()
        getChatById(idChat){ chat ->
            if (chat != null) {
                messagesChat = chat.messages
            }
        }
        return messagesChat
    }
}

fun getChatById(idChat: String, onResult: (Chat?) -> Unit) {
    val db = FirebaseFirestore.getInstance()

    db.collection("chats").document(idChat)
        .get()
        .addOnSuccessListener { document ->
            if (document != null && document.exists()) {
                val chat = document.toObject(Chat::class.java)
                onResult(chat)
            }
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

