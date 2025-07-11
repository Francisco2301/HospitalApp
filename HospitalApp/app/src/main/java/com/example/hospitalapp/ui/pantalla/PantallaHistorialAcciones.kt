package com.example.hospitalapp.ui.pantalla

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.FirebaseFirestore

data class EventoHistorial(
    val id: String = "",
    val tipo: String = "",
    val entidad: String = "",
    val usuario: String = "",
    val fecha: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaHistorialAcciones(
    rol: String,
    onVolver: () -> Unit
) {
    val puedeVer = rol == "admin" || rol == "superadmin"
    val eventos = remember { mutableStateListOf<EventoHistorial>() }
    var cargando by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(true) {
        FirebaseFirestore.getInstance()
            .collection("historial_acciones")
            .orderBy("fecha", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                eventos.clear()
                for (doc in result) {
                    eventos.add(
                        EventoHistorial(
                            id = doc.id,
                            tipo = doc.getString("tipo") ?: "",
                            entidad = doc.getString("entidad") ?: "",
                            usuario = doc.getString("usuario") ?: "",
                            fecha = doc.getString("fecha") ?: ""
                        )
                    )
                }
                cargando = false
            }
            .addOnFailureListener {
                error = it.message
                cargando = false
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Historial de acciones") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                !puedeVer -> {
                    Text(
                        "No tienes permisos para ver esta sección.",
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                cargando -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                error != null -> {
                    Text(
                        "Error: $error",
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        items(eventos) { evento ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text("Acción: ${evento.tipo}")
                                    Text("Entidad: ${evento.entidad}")
                                    Text("Usuario: ${evento.usuario}")
                                    Text("Fecha: ${evento.fecha}")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
