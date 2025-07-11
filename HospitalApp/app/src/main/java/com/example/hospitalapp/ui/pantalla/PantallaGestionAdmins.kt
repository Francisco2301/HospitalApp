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
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hospitalapp.viewmodel.AdminViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaGestionAdmins(
    onVolver: () -> Unit
) {
    val viewModel: AdminViewModel = viewModel()
    val usuarios = viewModel.usuariosConRol
    val usuarioEditando = viewModel.usuarioEditando
    val cargando = viewModel.cargando
    val mensajeError = viewModel.mensajeError

    var correo by remember { mutableStateOf(TextFieldValue("")) }
    var rol by remember { mutableStateOf("") }
    val rolesDisponibles = listOf("admin", "doctor", "asistente", "paciente")

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Precargar datos al seleccionar para editar
    LaunchedEffect(usuarioEditando) {
        usuarioEditando?.let {
            correo = TextFieldValue(it.first)
            rol = it.second
        } ?: run {
            correo = TextFieldValue("")
            rol = ""
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestión de Roles") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text("Registrar o editar rol", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(16.dp))

            TextField(
                value = correo,
                onValueChange = { correo = it },
                label = { Text("Correo electrónico") },
                modifier = Modifier.fillMaxWidth(),
                enabled = usuarioEditando == null
            )

            Spacer(Modifier.height(8.dp))

            // Dropdown para roles disponibles
            var expanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                TextField(
                    readOnly = true,
                    value = rol,
                    onValueChange = {},
                    label = { Text("Rol") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    rolesDisponibles.forEach { opcion ->
                        DropdownMenuItem(
                            text = { Text(opcion) },
                            onClick = {
                                rol = opcion
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = {
                        viewModel.agregarOActualizarUsuario(correo.text.trim(), rol.trim()) { exito, mensaje ->
                            scope.launch { snackbarHostState.showSnackbar(mensaje) }
                            if (exito) {
                                correo = TextFieldValue("")
                                rol = ""
                                viewModel.cancelarEdicion()
                            }
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(if (usuarioEditando == null) "Guardar" else "Actualizar")
                }

                if (usuarioEditando != null) {
                    Spacer(Modifier.width(8.dp))
                    OutlinedButton(
                        onClick = {
                            viewModel.cancelarEdicion()
                            correo = TextFieldValue("")
                            rol = ""
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancelar")
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            Text("Lista de usuarios con rol", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))

            if (cargando) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (mensajeError != null) {
                Text("Error: $mensajeError", color = MaterialTheme.colorScheme.error)
            } else {
                LazyColumn {
                    items(usuarios) { (correoUsuario, rolUsuario) ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            ListItem(
                                headlineContent = { Text(correoUsuario) },
                                supportingContent = { Text("Rol: $rolUsuario") },
                                trailingContent = {
                                    Row {
                                        IconButton(onClick = {
                                            viewModel.seleccionarParaEditar(correoUsuario to rolUsuario)
                                        }) {
                                            Icon(Icons.Default.Edit, contentDescription = "Editar")
                                        }
                                        IconButton(onClick = {
                                            viewModel.eliminarUsuario(correoUsuario) { exito, mensaje ->
                                                scope.launch {
                                                    snackbarHostState.showSnackbar(mensaje)
                                                }
                                            }
                                        }) {
                                            Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
