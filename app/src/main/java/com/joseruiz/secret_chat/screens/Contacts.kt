package com.joseruiz.secret_chat.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.joseruiz.secret_chat.R
import androidx.compose.animation.animateColorAsState
import com.joseruiz.secret_chat.data.User
import com.joseruiz.secret_chat.repository.getAllUsers
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactListView() {
    var contacts by remember { mutableStateOf<List<User>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) } // Estado de carga
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            contacts = getAllUsers()
            isLoading = false
        }
    }

    var isScrolled by remember { mutableStateOf(false) }

    val backgroundColor by animateColorAsState(
        targetValue = if (isScrolled) Color(0xFF075E54) else Color(0xFF128C7E), // Verde tipo WhatsApp
        animationSpec = androidx.compose.animation.core.tween(durationMillis = 600)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Contactos",
                        color = Color.White // Texto en blanco
                    )
                },
                navigationIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowBack, // Ícono de flecha
                        contentDescription = "Regresar",
                        modifier = Modifier
                            .clickable { /* Acción al regresar */ }
                            .padding(16.dp),
                        tint = Color.White // Ícono en blanco
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = backgroundColor // Aplicando color con animación
                )
            )
        }
    ) {
        Column(modifier = Modifier.padding(it)) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(contacts) { contact ->
                        ContactRow(contact)
                    }
                }
            }
        }
    }
}


@Composable
    fun ContactRow(contact: User) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* Acción al hacer clic en el contacto */ }
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.user),
            contentDescription = null,
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = contact.email,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color.Black
            )
            Text(
                text = "Último mensaje...",
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
    }
    Divider(color = Color.Gray, thickness = 0.5.dp)
}
