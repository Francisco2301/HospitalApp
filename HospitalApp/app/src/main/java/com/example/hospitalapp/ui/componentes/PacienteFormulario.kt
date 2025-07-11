package com.example.hospitalapp.ui.componentes

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.hospitalapp.modelo.Paciente

@Composable
fun PacienteFormulario(
    pacienteInicial: Paciente? = null,
    listaActual: List<Paciente> = emptyList(),
    onGuardar: (Paciente) -> Unit,
    onCancelar: (() -> Unit)? = null
) {
    var nombre by remember { mutableStateOf("") }
    var edadTexto by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }

    var fueTocadoNombre by remember { mutableStateOf(false) }
    var fueTocadoEdad by remember { mutableStateOf(false) }
    var fueTocadoTelefono by remember { mutableStateOf(false) }

    var errorMensaje by remember { mutableStateOf<String?>(null) }

    // Precarga de datos al editar
    LaunchedEffect(pacienteInicial) {
        pacienteInicial?.let {
            nombre = it.nombre
            edadTexto = it.edad.toString()
            telefono = it.telefono
        }
    }

    fun edadValida(): Boolean {
        val edad = edadTexto.toIntOrNull()
        return edad != null && edad in 0..120
    }

    fun telefonoValido(): Boolean {
        val regex = Regex("^\\d{4}\\s?\\d{4}$")
        return regex.matches(telefono.trim())
    }

    fun yaExiste(): Boolean {
        return listaActual.any {
            it.id != pacienteInicial?.id &&
                    it.nombre.equals(nombre.trim(), true) &&
                    it.telefono == telefono.trim()
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = nombre,
            onValueChange = {
                nombre = it
                fueTocadoNombre = true
            },
            label = { Text("Nombre del paciente") },
            modifier = Modifier.fillMaxWidth(),
            isError = fueTocadoNombre && nombre.isBlank()
        )

        OutlinedTextField(
            value = edadTexto,
            onValueChange = {
                edadTexto = it
                fueTocadoEdad = true
            },
            label = { Text("Edad") },
            modifier = Modifier.fillMaxWidth(),
            isError = fueTocadoEdad && !edadValida()
        )

        OutlinedTextField(
            value = telefono,
            onValueChange = {
                telefono = it
                fueTocadoTelefono = true
            },
            label = { Text("Teléfono") },
            modifier = Modifier.fillMaxWidth(),
            isError = fueTocadoTelefono && !telefonoValido()
        )

        errorMensaje?.let {
            Spacer(Modifier.height(8.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            onCancelar?.let {
                OutlinedButton(onClick = it) {
                    Text("Cancelar")
                }
            }

            Button(
                onClick = {
                    when {
                        nombre.isBlank() || edadTexto.isBlank() || telefono.isBlank() -> {
                            errorMensaje = "Todos los campos son obligatorios."
                        }
                        !edadValida() -> {
                            errorMensaje = "La edad debe estar entre 0 y 120 años."
                        }
                        !telefonoValido() -> {
                            errorMensaje = "El teléfono debe tener 8 dígitos (ej. 12345678 o 1234 5678)."
                        }
                        yaExiste() -> {
                            errorMensaje = "Ya existe un paciente con ese nombre y teléfono."
                        }
                        else -> {
                            errorMensaje = null
                            onGuardar(
                                Paciente(
                                    id = pacienteInicial?.id ?: "",
                                    nombre = nombre.trim(),
                                    edad = edadTexto.toInt(),
                                    telefono = telefono.trim()
                                )
                            )
                        }
                    }
                }
            ) {
                Text(if (pacienteInicial == null) "Guardar" else "Actualizar")
            }
        }
    }
}
