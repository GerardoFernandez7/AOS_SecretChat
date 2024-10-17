package com.joseruiz.secret_chat.data

data class Message(
    val text: String = "",
    val sentByUser: String = ""
) {
    // Constructor vacío para Firestore
    constructor() : this("", "")
}