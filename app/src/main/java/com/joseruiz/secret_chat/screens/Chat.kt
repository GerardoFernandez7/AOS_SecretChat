package com.joseruiz.secret_chat.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.joseruiz.secret_chat.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.joseruiz.secret_chat.data.Message
import com.joseruiz.secret_chat.viewModel.buscarChatPorId
import com.joseruiz.secret_chat.viewModel.sendMessage

var emailLoged: String = ""
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(email: String) {
    // Se captura el usuario logeado
    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    val currentUser: FirebaseUser? = auth.currentUser
    emailLoged = currentUser?.email.toString()

    // Estado para los mensajes
    val messagesChat = remember { mutableStateListOf<Message>() }

    // Generar el idChat
    val idChat = generateChatId(emailLoged, email)

    // Estado para controlar si el modal del PIN está visible
    var showPinDialog by remember { mutableStateOf(true) }
    var pin by remember { mutableStateOf("") }

    // Escuchar cambios en tiempo real en Firebase fuera del diálogo
    LaunchedEffect(idChat) {
        val db = FirebaseFirestore.getInstance()
        db.collection("chats").document(idChat)
            .collection("messages")
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.w("ChatScreen", "Error al escuchar cambios en los mensajes", e)
                    return@addSnapshotListener
                }

                if (snapshots != null) {
                    val updatedMessages = snapshots.toObjects(Message::class.java)
                    messagesChat.clear()
                    messagesChat.addAll(updatedMessages)
                }
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Regresar",
                            modifier = Modifier.clickable { /* Acción al regresar */ },
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Image(
                            painter = painterResource(R.drawable.user),
                            contentDescription = null,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = email, color = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF128C7E) // Verde similar al de WhatsApp
                )
            )
        },
        bottomBar = {
            ChatInputBar(idChat, emailLoged, messagesChat)
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(Color(0xFFEDEDED)),
            contentPadding = PaddingValues(8.dp)
        ) {
            items(messagesChat) { message ->
                Log.d("ChatScreen", "Mensaje: ${message.text}, Enviado por: ${message.sentByUser}")
                Log.d("ChatScreen", "Email logeado: $emailLoged")

                ChatBubble(message, emailLoged)
            }
        }
    }

    // Mostrar el dialog para solicitar el PIN sobre la pantalla del chat
    if (showPinDialog) {
        PinDialog(idChat = idChat, pin = pin, onPinChange = { pin = it }, onConfirm = {
            // Llamada asíncrona para buscar mensajes iniciales usando el PIN
            buscarChatPorId(idChat, pin) { messages ->
                messagesChat.clear()
                messagesChat.addAll(messages)
                showPinDialog = false
            }
        })
    }
}



@Composable
fun PinDialog(idChat: String, pin: String, onPinChange: (String) -> Unit, onConfirm: () -> Unit) {
    Dialog(onDismissRequest = {}) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Ingrese su PIN", fontSize = 18.sp, color = Color.Black)
                Spacer(modifier = Modifier.height(16.dp))

                // Input para el PIN
                TextField(
                    value = pin,
                    onValueChange = onPinChange,
                    placeholder = { Text("PIN") },
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = {
                    onConfirm()
                }) {
                    Text(text = "Aceptar")
                }
            }
        }
    }
}



fun generateChatId(email1: String, email2: String): String {
    return if (email1.compareTo(email2) <= 0) {
        email1 + email2
    } else {
        email2 + email1
    }
}


@Composable
fun ChatBubble(message: Message, userLoged: String) {
    val isCurrentUser = message.sentByUser == userLoged
    val alignment = if (isCurrentUser) Alignment.End else Alignment.Start
    val backgroundColor = if (isCurrentUser) Color(0xFFE1FFC7) else Color.White
    val textColor = if (isCurrentUser) Color.Black else Color.Black

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isCurrentUser) Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .background(backgroundColor)
                .padding(8.dp)
                .widthIn(max = 250.dp)
        ) {
            Text(
                text = message.text,
                fontSize = 16.sp,
                color = textColor
            )
        }
    }
    Spacer(modifier = Modifier.height(4.dp))
}


@Composable
fun ChatInputBar(idChat: String, isSentByUser: String, messagesChat: SnapshotStateList<Message>) {
    var text by remember { mutableStateOf("") }

    // Control del foco
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BasicTextField(
            value = text,
            onValueChange = { text = it },
            modifier = Modifier
                .weight(1f)
                .padding(8.dp)
                .background(Color(0xFFF0F0F0), CircleShape)
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .focusRequester(focusRequester),
            decorationBox = { innerTextField ->
                if (text.isEmpty()) {
                    Text("Escribe un mensaje...", color = Color.Gray)
                }
                innerTextField()
            },
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Send
            ),
            keyboardActions = KeyboardActions(
                onSend = {
                    if (text.isNotBlank()) {
                        // Crear un nuevo mensaje localmente
                        val newMessage = Message(text = text, sentByUser = isSentByUser)
                        messagesChat.add(newMessage)  // Actualizar la lista de mensajes local

                        // Enviar el mensaje a Firebase
                        sendMessage(idChat, text, isSentByUser)
                        text = ""  // Limpiar el campo de texto
                        keyboardController?.hide()  // Esconder el teclado
                        focusManager.clearFocus()  // Limpiar el foco
                    }
                }
            )
        )
        IconButton(onClick = {
            if (text.isNotBlank()) {
                // Crear un nuevo mensaje localmente
                val newMessage = Message(text = text, sentByUser = isSentByUser)
                messagesChat.add(newMessage)  // Actualizar la lista de mensajes local

                // Enviar el mensaje a Firebase
                sendMessage(idChat, text, isSentByUser)
                text = ""  // Limpiar el campo de texto
                keyboardController?.hide()  // Esconder el teclado
            }
        }) {
            Icon(imageVector = Icons.Default.Send, contentDescription = "Enviar", tint = Color(0xFF128C7E))
        }
    }

    // Asegura que el teclado se abra cuando el composable es cargado
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}