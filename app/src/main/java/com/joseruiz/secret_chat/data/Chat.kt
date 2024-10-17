package com.joseruiz.secret_chat.data

data class Chat(
    val messages: List<String>,
    val users: List<User>
)