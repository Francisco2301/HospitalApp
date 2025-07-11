package com.example.hospitalapp.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import com.example.hospitalapp.modelo.Paciente
import com.example.hospitalapp.repositorio.PacienteRepositorio
import com.example.hospitalapp.repositorio.HistorialRepositorio

class PacienteViewModel : ViewModel() {

    var pacientes by mutableStateOf<List<Paciente>>(emptyList())
        private set

    var cargando by mutableStateOf(false)
        private set

    var mensajeError by mutableStateOf<String?>(null)
        private set

    var pacienteEditando: Paciente? by mutableStateOf(null)
        private set

    init {
        cargarPacientes()
    }

    fun cargarPacientes() {
        cargando = true
        PacienteRepositorio.obtenerPacientes(
            onSuccess = {
                pacientes = it
                cargando = false
            },
            onError = {
                mensajeError = it.message
                cargando = false
            }
        )
    }

    fun agregarPaciente(nuevo: Paciente, onResultado: (Boolean) -> Unit) {
        cargando = true
        PacienteRepositorio.agregarPaciente(
            paciente = nuevo,
            onSuccess = {
                cargarPacientes()
                HistorialRepositorio.registrarAccion(
                    tipo = "crear",
                    entidad = "paciente",
                    idEntidad = nuevo.id,
                    descripcion = "Paciente ${nuevo.nombre} creado"
                )
                onResultado(true)
            },
            onError = {
                mensajeError = it.message
                cargando = false
                onResultado(false)
            }
        )
    }

    fun seleccionarPacienteParaEditar(paciente: Paciente) {
        pacienteEditando = paciente
    }

    fun limpiarEdicion() {
        pacienteEditando = null
    }

    fun actualizarPacienteEditado(onResultado: (Boolean) -> Unit) {
        val actual = pacienteEditando
        if (actual != null) {
            cargando = true
            PacienteRepositorio.actualizarPaciente(
                id = actual.id,
                paciente = actual,
                onSuccess = {
                    limpiarEdicion()
                    cargarPacientes()
                    HistorialRepositorio.registrarAccion(
                        tipo = "editar",
                        entidad = "paciente",
                        idEntidad = actual.id,
                        descripcion = "Paciente ${actual.nombre} actualizado"
                    )
                    onResultado(true)
                },
                onError = {
                    mensajeError = it.message
                    cargando = false
                    onResultado(false)
                }
            )
        }
    }

    fun eliminarPaciente(id: String, onResultado: (Boolean) -> Unit) {
        cargando = true
        PacienteRepositorio.eliminarPaciente(
            id = id,
            onSuccess = {
                cargarPacientes()
                HistorialRepositorio.registrarAccion(
                    tipo = "eliminar",
                    entidad = "paciente",
                    idEntidad = id,
                    descripcion = "Paciente con ID $id eliminado"
                )
                onResultado(true)
            },
            onError = {
                mensajeError = it.message
                cargando = false
                onResultado(false)
            }
        )
    }
}
