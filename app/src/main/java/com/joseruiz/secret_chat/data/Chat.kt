package com.joseruiz.secret_chat.data

data class Chat(
    val idChat: String,
    val pin: String,
    val messages: List<Message>
)