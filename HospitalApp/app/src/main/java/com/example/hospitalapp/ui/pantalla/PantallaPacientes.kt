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
import com.example.hospitalapp.ui.componentes.PacienteFormulario
import com.example.hospitalapp.viewmodel.PacienteViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaPacientes(
    onVolver: () -> Unit,
    rolUsuario: String
) {
    val puedeEditar = rolUsuario in listOf("admin", "asistente", "superadmin")
    val viewModel: PacienteViewModel = viewModel()
    val pacientes = viewModel.pacientes
    val cargando = viewModel.cargando
    val mensajeError = viewModel.mensajeError
    val pacienteEditando by viewModel::pacienteEditando
    val context = LocalContext.current

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var idParaEliminar by remember { mutableStateOf<String?>(null) }

    var ultimaActualizacion by remember { mutableStateOf(obtenerHoraActual()) }

    fun recargar() {
        viewModel.cargarPacientes()
        ultimaActualizacion = obtenerHoraActual()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pacientes") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = { recargar() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Actualizar")
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
            Text("Gestión de Pacientes", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(4.dp))
            Text("Última actualización: $ultimaActualizacion", style = MaterialTheme.typography.labelMedium)
            Spacer(Modifier.height(16.dp))

            if (puedeEditar) {
                PacienteFormulario(
                    pacienteInicial = pacienteEditando,
                    listaActual = pacientes,
                    onGuardar = { nuevo ->
                        if (pacienteEditando == null) {
                            viewModel.agregarPaciente(nuevo) { exito ->
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        if (exito) "Paciente guardado" else "Error al guardar"
                                    )
                                }
                            }
                        } else {
                            viewModel.seleccionarPacienteParaEditar(nuevo)
                            viewModel.actualizarPacienteEditado { exito ->
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        if (exito) "Paciente actualizado" else "Error al actualizar"
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

                pacientes.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No hay pacientes registrados aún.")
                    }
                }

                else -> {
                    LazyColumn {
                        items(pacientes) { paciente ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text("Nombre: ${paciente.nombre}")
                                    Text("Edad: ${paciente.edad}")
                                    Text(
                                        text = "Teléfono: ${paciente.telefono}",
                                        modifier = Modifier.clickable {
                                            val intent = Intent(Intent.ACTION_DIAL).apply {
                                                data = Uri.parse("tel:${paciente.telefono}")
                                            }
                                            context.startActivity(intent)
                                        },
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.primary
                                    )

                                    if (puedeEditar) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(top = 8.dp),
                                            horizontalArrangement = Arrangement.End
                                        ) {
                                            IconButton(onClick = {
                                                viewModel.seleccionarPacienteParaEditar(paciente)
                                            }) {
                                                Icon(Icons.Default.Edit, contentDescription = "Editar")
                                            }
                                            IconButton(onClick = {
                                                idParaEliminar = paciente.id
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
                title = { Text("Eliminar paciente") },
                text = { Text("¿Estás seguro de eliminar este paciente?") },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.eliminarPaciente(id) { exito ->
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    if (exito) "Paciente eliminado" else "Error al eliminar"
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

private fun obtenerHoraActual(): String {
    val formato = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    return formato.format(Date())
}
