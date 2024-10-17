package com.joseruiz.secret_chat.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.joseruiz.secret_chat.R
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.runtime.LaunchedEffect

data class Message(val text: String, val isSentByUser: Boolean)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(messages: List<Message>) {
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
                        Text(text = "Alex Tremo", color = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF128C7E) // Verde similar al de WhatsApp
                )
            )
        },
        bottomBar = {
            ChatInputBar()
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(Color(0xFFEDEDED)),
            contentPadding = PaddingValues(8.dp)
        ) {

            items(messages) { message ->
                ChatBubble(message)
            }
        }
    }
}

@Composable
fun ChatBubble(message: Message) {
    val alignment = if (message.isSentByUser) Alignment.End else Alignment.Start
    val backgroundColor = if (message.isSentByUser) Color(0xFFE1FFC7) else Color.White
    val textColor = if (message.isSentByUser) Color.Black else Color.Black

    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = if (message.isSentByUser) Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .background(backgroundColor)
                .padding(8.dp)
                .widthIn(max = 250.dp) // Limitar el ancho de los mensajes
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
fun ChatInputBar() {
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
                    keyboardController?.hide()  // Esconde el teclado después de enviar el mensaje
                    focusManager.clearFocus()  // Limpia el foco
                }
            )
        )
        IconButton(onClick = {
            // Lógica para enviar el mensaje
            keyboardController?.hide()  // Esconde el teclado después de enviar
        }) {
            Icon(imageVector = Icons.Default.Send, contentDescription = "Enviar", tint = Color(0xFF128C7E))
        }
    }

    // Asegura que el teclado se abra cuando el composable es cargado
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}