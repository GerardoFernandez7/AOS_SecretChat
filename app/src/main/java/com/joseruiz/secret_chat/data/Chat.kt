package com.joseruiz.secret_chat.data

data class Chat(
    val user1: User,
    val user2: User,
    val messages: List<Message>
)