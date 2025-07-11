package com.example.hospitalapp.ui.componentes

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.hospitalapp.modelo.Cita
import com.example.hospitalapp.modelo.Doctor
import com.example.hospitalapp.modelo.Paciente
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CitaFormulario(
    citaInicial: Cita? = null,
    listaDoctores: List<Doctor>,
    listaPacientes: List<Paciente>,
    listaActual: List<Cita> = emptyList(),
    onGuardar: (Cita) -> Unit,
    onCancelar: (() -> Unit)? = null
) {
    val context = LocalContext.current

    var doctorSeleccionado by remember { mutableStateOf<Doctor?>(null) }
    var pacienteSeleccionado by remember { mutableStateOf<Paciente?>(null) }
    var fechaSeleccionada by remember { mutableStateOf("") }
    var horaSeleccionada by remember { mutableStateOf("") }

    var mostrarMenuDoctor by remember { mutableStateOf(false) }
    var mostrarMenuPaciente by remember { mutableStateOf(false) }

    var errorMensaje by remember { mutableStateOf<String?>(null) }

    // Precargar datos
    LaunchedEffect(citaInicial) {
        citaInicial?.let {
            doctorSeleccionado = listaDoctores.find { d -> d.id == it.idDoctor }
            pacienteSeleccionado = listaPacientes.find { p -> p.id == it.idPaciente }
            fechaSeleccionada = it.fecha
            horaSeleccionada = it.hora
        }
    }

    fun citaYaExiste(): Boolean {
        return listaActual.any {
            it.id != citaInicial?.id &&
                    it.idDoctor == doctorSeleccionado?.id &&
                    it.fecha == fechaSeleccionada &&
                    it.hora == horaSeleccionada
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        // Selector Doctor
        ExposedDropdownMenuBox(
            expanded = mostrarMenuDoctor,
            onExpandedChange = { mostrarMenuDoctor = !mostrarMenuDoctor }
        ) {
            TextField(
                value = doctorSeleccionado?.nombre ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("Seleccionar Doctor") },
                trailingIcon = { Icon(Icons.Filled.ArrowDropDown, null) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )
            DropdownMenu(
                expanded = mostrarMenuDoctor,
                onDismissRequest = { mostrarMenuDoctor = false }
            ) {
                listaDoctores.forEach { doctor ->
                    DropdownMenuItem(
                        text = { Text(doctor.nombre) },
                        onClick = {
                            doctorSeleccionado = doctor
                            mostrarMenuDoctor = false
                        }
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        // Selector Paciente
        ExposedDropdownMenuBox(
            expanded = mostrarMenuPaciente,
            onExpandedChange = { mostrarMenuPaciente = !mostrarMenuPaciente }
        ) {
            TextField(
                value = pacienteSeleccionado?.nombre ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("Seleccionar Paciente") },
                trailingIcon = { Icon(Icons.Filled.ArrowDropDown, null) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )
            DropdownMenu(
                expanded = mostrarMenuPaciente,
                onDismissRequest = { mostrarMenuPaciente = false }
            ) {
                listaPacientes.forEach { paciente ->
                    DropdownMenuItem(
                        text = { Text(paciente.nombre) },
                        onClick = {
                            pacienteSeleccionado = paciente
                            mostrarMenuPaciente = false
                        }
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        // Selector Fecha
        Button(
            onClick = {
                val calendario = Calendar.getInstance()
                DatePickerDialog(
                    context,
                    { _, y, m, d -> fechaSeleccionada = "$d/${m + 1}/$y" },
                    calendario.get(Calendar.YEAR),
                    calendario.get(Calendar.MONTH),
                    calendario.get(Calendar.DAY_OF_MONTH)
                ).show()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (fechaSeleccionada.isBlank()) "Seleccionar Fecha" else "Fecha: $fechaSeleccionada")
        }

        Spacer(Modifier.height(8.dp))

        // Selector Hora
        Button(
            onClick = {
                val calendario = Calendar.getInstance()
                TimePickerDialog(
                    context,
                    { _, hour, minute ->
                        val amPm = if (hour < 12) "AM" else "PM"
                        val horaFormateada = String.format("%02d:%02d %s", hour % 12, minute, amPm)
                        horaSeleccionada = horaFormateada
                    },
                    calendario.get(Calendar.HOUR_OF_DAY),
                    calendario.get(Calendar.MINUTE),
                    false
                ).show()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (horaSeleccionada.isBlank()) "Seleccionar Hora" else "Hora: $horaSeleccionada")
        }

        errorMensaje?.let {
            Spacer(Modifier.height(8.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        Spacer(Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            onCancelar?.let {
                OutlinedButton(onClick = it) {
                    Text("Cancelar")
                }
            }

            Button(onClick = {
                when {
                    doctorSeleccionado == null || pacienteSeleccionado == null ||
                            fechaSeleccionada.isBlank() || horaSeleccionada.isBlank() -> {
                        errorMensaje = "Todos los campos son obligatorios."
                    }
                    citaYaExiste() -> {
                        errorMensaje = "Ya hay una cita con ese doctor en ese horario."
                    }
                    else -> {
                        errorMensaje = null
                        onGuardar(
                            Cita(
                                id = citaInicial?.id ?: "",
                                idDoctor = doctorSeleccionado!!.id,
                                nombreDoctor = doctorSeleccionado!!.nombre,
                                idPaciente = pacienteSeleccionado!!.id,
                                nombrePaciente = pacienteSeleccionado!!.nombre,
                                fecha = fechaSeleccionada,
                                hora = horaSeleccionada
                            )
                        )
                    }
                }
            }) {
                Text(if (citaInicial == null) "Guardar" else "Actualizar")
            }
        }
    }
}
