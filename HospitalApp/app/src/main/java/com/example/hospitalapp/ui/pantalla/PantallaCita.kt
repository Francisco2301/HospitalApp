package com.example.hospitalapp.ui.pantalla

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hospitalapp.viewmodel.CitaViewModel
import com.example.hospitalapp.viewmodel.DoctorViewModel
import com.example.hospitalapp.viewmodel.PacienteViewModel
import com.example.hospitalapp.ui.componentes.CitaFormulario
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaCita(
    onVolver: () -> Unit,
    rolUsuario: String
) {
    val puedeEditar = rolUsuario in listOf("admin", "superadmin", "doctor", "asistente")
    val viewModel: CitaViewModel = viewModel()
    val doctoresVM: DoctorViewModel = viewModel()
    val pacientesVM: PacienteViewModel = viewModel()

    val citas = viewModel.citas
    val cargando = viewModel.cargando
    val mensajeError = viewModel.mensajeError
    val citaEditando by viewModel::citaEditando
    val doctores = doctoresVM.doctores
    val pacientes = pacientesVM.pacientes

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var idParaEliminar by remember { mutableStateOf<String?>(null) }

    var ultimaActualizacion by remember { mutableStateOf(obtenerHoraActual()) }

    fun recargar() {
        viewModel.cargarCitas()
        doctoresVM.cargarDoctores()
        pacientesVM.cargarPacientes()
        ultimaActualizacion = obtenerHoraActual()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Citas") },
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
            Text("Gestión de Citas", style = MaterialTheme.typography.headlineSmall)
            Text("Última actualización: $ultimaActualizacion", style = MaterialTheme.typography.labelMedium)
            Spacer(Modifier.height(16.dp))

            if (puedeEditar) {
                CitaFormulario(
                    citaInicial = citaEditando,
                    listaDoctores = doctores,
                    listaPacientes = pacientes,
                    listaActual = citas,
                    onGuardar = { cita ->
                        if (citaEditando == null) {
                            viewModel.agregarCita(cita) { exito ->
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        if (exito) "Cita guardada" else "Error al guardar"
                                    )
                                }
                            }
                        } else {
                            viewModel.seleccionarCitaParaEditar(cita)
                            viewModel.actualizarCitaEditada { exito ->
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        if (exito) "Cita actualizada" else "Error al actualizar"
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

                citas.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No hay citas registradas aún.")
                    }
                }

                else -> {
                    LazyColumn {
                        items(citas) { cita ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    // Text("ID: ${cita.id}", style = MaterialTheme.typography.labelSmall)
                                    Text("Paciente: ${cita.nombrePaciente}")
                                    Text("Doctor: ${cita.nombreDoctor}")
                                    Text("Fecha: ${cita.fecha}")
                                    Text("Hora: ${cita.hora}")

                                    if (puedeEditar) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(top = 8.dp),
                                            horizontalArrangement = Arrangement.End
                                        ) {
                                            IconButton(onClick = {
                                                viewModel.seleccionarCitaParaEditar(cita)
                                            }) {
                                                Icon(Icons.Default.Edit, contentDescription = "Editar")
                                            }
                                            IconButton(onClick = {
                                                idParaEliminar = cita.id
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
                title = { Text("Eliminar cita") },
                text = { Text("¿Estás seguro de eliminar esta cita?") },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.eliminarCita(id) { exito ->
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    if (exito) "Cita eliminada" else "Error al eliminar"
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
