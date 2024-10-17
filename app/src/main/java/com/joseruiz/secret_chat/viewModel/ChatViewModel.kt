package com.joseruiz.secret_chat.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.joseruiz.secret_chat.data.Chat
import com.joseruiz.secret_chat.data.Message
import kotlin.experimental.xor


fun buscarChatPorId(idChat: String, pin: String, onResult: (List<Message>) -> Unit) {
    val db = FirebaseFirestore.getInstance()

    db.collection("chats")
        .whereEqualTo("idChat", idChat)
        .get()
        .addOnSuccessListener { querySnapshot: QuerySnapshot ->
            if (!querySnapshot.isEmpty) {
                createChat(idChat, true, pin) { messages ->
                    onResult(messages) // Retorna la lista de mensajes obtenidos
                }
            } else {
                createChat(idChat, false, pin) { messages ->
                    onResult(messages) // Retorna la lista de mensajes de un nuevo chat
                }
            }
        }
        .addOnFailureListener { exception ->
            Log.w("Firebase", "Error buscando chat", exception)
            onResult(listOf()) // Devuelve una lista vacía en caso de error
        }
}

fun createChat(idChat: String, exists: Boolean, pin: String, onResult: (List<Message>) -> Unit) {
    val db = FirebaseFirestore.getInstance()
    if (!exists) { // Si no existe el chat
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
                onResult(newChat.messages) // Retorna los mensajes del nuevo chat
            }
            .addOnFailureListener { exception ->
                Log.w("Firebase", "Error al crear el chat", exception)
                onResult(listOf()) // Devuelve una lista vacía en caso de error
            }
    } else { // Si existe el chat, entonces se obtiene la data
        getChatById(idChat) { chat ->
            if (chat != null) {
                onResult(chat.messages) // Retorna los mensajes del chat existente
            } else {
                onResult(listOf()) // Devuelve una lista vacía si el chat no se encontró
            }
        }
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
            } else {
                onResult(null) // Devuelve null si no se encontró el chat
            }
        }
        .addOnFailureListener {
            onResult(null) // Devuelve null en caso de error
        }
}


fun sendMessage(idChat: String, messageText: String, isSentByUser: String) {
    val db = FirebaseFirestore.getInstance()
    db.collection("chats").document(idChat)
        .get()
        .addOnSuccessListener { document ->
            if (document != null && document.exists()) {
                val chat = document.toObject(Chat::class.java)
                val newMessage = encryptMessage( Message(text = messageText, sentByUser = isSentByUser),chat.pin)
                if (chat != null) {
                    val updatedMessages = chat.messages.toMutableList()
                    updatedMessages.add(newMessage)
                    val updatedChat = chat.copy(messages = updatedMessages)
                    db.collection("chats").document(idChat)
                        .set(updatedChat)
                        .addOnSuccessListener {
                            Log.d("Firebase", "Chat actualizado con éxito")
                        }
                        .addOnFailureListener { e ->
                            Log.w("Firebase", "Error al actualizar el chat", e)
                        }
                }
            } else {
                Log.w("Firebase", "El chat no existe")
            }
        }
        .addOnFailureListener { e ->
            Log.w("Firebase", "Error al obtener el chat", e)
        }
}


fun encryptMessage(message: Message, key: String): Message {
    val keyBytes = key.toByteArray()

    // Convierte el texto del mensaje en bytes
    val textBytes = message.text.toByteArray()

    // Aplica XOR con los bytes de la clave
    val encryptedText = textBytes.mapIndexed { index, byte ->
        byte xor keyBytes[index % keyBytes.size]
    }.toByteArray()

    // Codifica el texto encriptado en Base64
    val encodedText = android.util.Base64.encodeToString(encryptedText, android.util.Base64.DEFAULT)

    // Retorna un nuevo objeto Message con el texto encriptado
    return Message(
        text = encodedText,
        sentByUser = message.sentByUser
    )
}

fun decryptMessage(encryptedMessages: List<Message>, key: String): List<Message> {
    val keyBytes = key.toByteArray()
    val decryptedMessages = mutableListOf<Message>()

    encryptedMessages.forEach { message ->
        val encryptedBytes = android.util.Base64.decode(message.text, android.util.Base64.DEFAULT)

        val decryptedText = encryptedBytes.mapIndexed { index, byte ->
            byte xor keyBytes[index % keyBytes.size]
        }.toByteArray()

        // Convierte los bytes desencriptados en texto legible
        val decryptedString = String(decryptedText)

        // Crea un nuevo mensaje con el texto desencriptado
        decryptedMessages.add(
            Message(
                text = decryptedString,
                sentByUser = message.sentByUser
            )
        )
    }
    return decryptedMessages
}

