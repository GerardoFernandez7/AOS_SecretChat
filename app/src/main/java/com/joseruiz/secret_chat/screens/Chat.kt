package com.joseruiz.secret_chat.screens

import android.provider.ContactsContract.CommonDataKinds.Email
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.joseruiz.secret_chat.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.joseruiz.secret_chat.viewModel.buscarChatPorId


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(email: String) {
    // Se captura el usuario logeado
    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    val currentUser: FirebaseUser? = auth.currentUser
    var emailLoged: String = ""
    if (currentUser != null) {
        emailLoged = currentUser.email.toString()
    } else {
        println("Ningún usuario ha iniciado sesión")
    }

    val idChat = generateChatId(emailLoged, email)

    // Estado para controlar si el modal del PIN está visible
    var showPinDialog by remember { mutableStateOf(true) }
    var pin by remember { mutableStateOf("") }

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
            // ChatInputBar()
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(Color(0xFFEDEDED)),
            contentPadding = PaddingValues(8.dp)
        ) {
            // Aquí irían los mensajes del chat
            /*
            items(messages) { message ->
                ChatBubble(message)
            }
            */
        }
    }

    // Mostrar el dialog para solicitar el PIN sobre la pantalla del chat
    if (showPinDialog) {
        PinDialog(idChat= idChat, pin = pin, onPinChange = { pin = it }, onConfirm = {
            showPinDialog = false
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
                    buscarChatPorId(idChat, pin)
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
