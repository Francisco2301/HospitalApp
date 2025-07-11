package com.example.hospitalapp.ui.pantalla

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hospitalapp.modelo.Doctor
import com.example.hospitalapp.viewmodel.DoctorViewModel
import com.example.hospitalapp.ui.componentes.DoctorFormulario
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaDoctores(
    onVolver: () -> Unit,
    rolUsuario: String
) {
    val puedeEditar = rolUsuario in listOf("admin", "superadmin")
    val viewModel: DoctorViewModel = viewModel()
    val doctores = viewModel.doctores
    val cargando = viewModel.cargando
    val mensajeError = viewModel.mensajeError
    val doctorEditando by viewModel::doctorEditando
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var idParaEliminar by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Doctores") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text("Gestión de Doctores", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(16.dp))

            if (puedeEditar) {
                DoctorFormulario(
                    doctorInicial = doctorEditando,
                    listaActual = doctores,
                    onGuardar = { nuevo ->
                        if (doctorEditando == null) {
                            viewModel.agregarDoctor(nuevo) { exito ->
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        if (exito) "Doctor guardado" else "Error al guardar"
                                    )
                                }
                            }
                        } else {
                            viewModel.seleccionarDoctorParaEditar(nuevo)
                            viewModel.actualizarDoctorEditado { exito ->
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        if (exito) "Doctor actualizado" else "Error al actualizar"
                                    )
                                }
                            }
                        }
                    },
                    onCancelar = { viewModel.limpiarEdicion() }
                )
                Spacer(Modifier.height(24.dp))
            }

            when {
                cargando -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                mensajeError != null -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Error: $mensajeError")
                    }
                }
                doctores.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No hay doctores registrados aún.")
                    }
                }
                else -> {
                    val context = LocalContext.current

                    LazyColumn {
                        items(doctores) { doctor ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text("Nombre: ${doctor.nombre}")
                                    Text("Especialidad: ${doctor.especialidad}")

                                    Text(
                                        text = "Teléfono: ${doctor.telefono}",
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.clickable {
                                            val intent = Intent(Intent.ACTION_DIAL).apply {
                                                data = Uri.parse("tel:${doctor.telefono}")
                                            }
                                            context.startActivity(intent)
                                        }
                                    )

                                    Text(
                                        text = "Correo: ${doctor.correo}",
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.clickable {
                                            val intent = Intent(Intent.ACTION_SENDTO).apply {
                                                data = Uri.parse("mailto:${doctor.correo}")
                                            }
                                            context.startActivity(intent)
                                        }
                                    )

                                    if (puedeEditar) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(top = 8.dp),
                                            horizontalArrangement = Arrangement.End
                                        ) {
                                            IconButton(onClick = {
                                                viewModel.seleccionarDoctorParaEditar(doctor)
                                            }) {
                                                Icon(Icons.Default.Edit, contentDescription = "Editar")
                                            }
                                            IconButton(onClick = {
                                                idParaEliminar = doctor.id
                                            }) {
                                                Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        idParaEliminar?.let { id ->
            AlertDialog(
                onDismissRequest = { idParaEliminar = null },
                title = { Text("Eliminar doctor") },
                text = { Text("¿Estás seguro de eliminar este doctor?") },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.eliminarDoctor(id) { exito ->
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    if (exito) "Doctor eliminado" else "Error al eliminar"
                                )
                            }
                        }
                        idParaEliminar = null
                    }) {
                        Text("Eliminar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { idParaEliminar = null }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}
