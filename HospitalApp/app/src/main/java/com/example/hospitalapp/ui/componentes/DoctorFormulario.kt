package com.example.hospitalapp.ui.componentes

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.hospitalapp.modelo.Doctor

@Composable
fun DoctorFormulario(
    doctorInicial: Doctor? = null,
    listaActual: List<Doctor> = emptyList(),
    onGuardar: (Doctor) -> Unit,
    onCancelar: (() -> Unit)? = null
) {
    var nombre by remember { mutableStateOf("") }
    var especialidad by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }

    var touchedNombre by remember { mutableStateOf(false) }
    var touchedEspecialidad by remember { mutableStateOf(false) }
    var touchedTelefono by remember { mutableStateOf(false) }
    var touchedCorreo by remember { mutableStateOf(false) }

    var mensajeError by remember { mutableStateOf<String?>(null) }

    // Precarga de datos
    LaunchedEffect(doctorInicial) {
        doctorInicial?.let {
            nombre = it.nombre
            especialidad = it.especialidad
            telefono = it.telefono
            correo = it.correo
        }
    }

    fun correoValido(): Boolean = correo.contains("@") && correo.contains(".")

    fun telefonoValido(): Boolean {
        val regex = Regex("^\\d{4}\\s?\\d{4}$")
        return regex.matches(telefono.trim())
    }

    fun yaExiste(): Boolean {
        return listaActual.any {
            it.id != doctorInicial?.id &&
                    it.nombre.equals(nombre.trim(), true) &&
                    it.especialidad.equals(especialidad.trim(), true)
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = nombre,
            onValueChange = {
                nombre = it
                touchedNombre = true
            },
            label = { Text("Nombre del doctor") },
            modifier = Modifier.fillMaxWidth(),
            isError = touchedNombre && nombre.isBlank()
        )

        OutlinedTextField(
            value = especialidad,
            onValueChange = {
                especialidad = it
                touchedEspecialidad = true
            },
            label = { Text("Especialidad") },
            modifier = Modifier.fillMaxWidth(),
            isError = touchedEspecialidad && especialidad.isBlank()
        )

        OutlinedTextField(
            value = telefono,
            onValueChange = {
                telefono = it
                touchedTelefono = true
            },
            label = { Text("Teléfono") },
            modifier = Modifier.fillMaxWidth(),
            isError = touchedTelefono && !telefonoValido()
        )

        OutlinedTextField(
            value = correo,
            onValueChange = {
                correo = it
                touchedCorreo = true
            },
            label = { Text("Correo electrónico") },
            modifier = Modifier.fillMaxWidth(),
            isError = touchedCorreo && !correoValido()
        )

        mensajeError?.let {
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

            Button(onClick = {
                when {
                    nombre.isBlank() || especialidad.isBlank() || telefono.isBlank() || correo.isBlank() -> {
                        mensajeError = "Todos los campos son obligatorios."
                    }
                    !telefonoValido() -> {
                        mensajeError = "El teléfono debe tener 8 dígitos (ej. 12345678 o 1234 5678)."
                    }
                    !correoValido() -> {
                        mensajeError = "El correo no es válido."
                    }
                    yaExiste() -> {
                        mensajeError = "Ya existe un doctor con ese nombre y especialidad."
                    }
                    else -> {
                        mensajeError = null
                        onGuardar(
                            Doctor(
                                id = doctorInicial?.id ?: "",
                                nombre = nombre.trim(),
                                especialidad = especialidad.trim(),
                                telefono = telefono.trim(),
                                correo = correo.trim()
                            )
                        )
                    }
                }
            }) {
                Text(if (doctorInicial == null) "Guardar" else "Actualizar")
            }
        }
    }
}
