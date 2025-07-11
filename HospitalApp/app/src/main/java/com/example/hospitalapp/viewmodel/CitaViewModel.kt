package com.example.hospitalapp.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import com.example.hospitalapp.modelo.Cita
import com.example.hospitalapp.repositorio.CitaRepositorio
import com.example.hospitalapp.repositorio.HistorialRepositorio

class CitaViewModel : ViewModel() {

    var citas by mutableStateOf<List<Cita>>(emptyList())
        private set

    var cargando by mutableStateOf(false)
        private set

    var mensajeError by mutableStateOf<String?>(null)
        private set

    var citaEditando: Cita? by mutableStateOf(null)

    init {
        cargarCitas()
    }

    fun cargarCitas() {
        cargando = true
        CitaRepositorio.obtenerCitas(
            onSuccess = {
                citas = it
                cargando = false
            },
            onError = {
                mensajeError = it.message
                cargando = false
            }
        )
    }

    fun agregarCita(cita: Cita, onResultado: (Boolean) -> Unit) {
        cargando = true
        CitaRepositorio.agregarCita(
            cita = cita,
            onSuccess = {
                cargarCitas()
                HistorialRepositorio.registrarAccion(
                    tipo = "crear",
                    entidad = "cita",
                    idEntidad = cita.id,
                    descripcion = "Cita para ${cita.nombrePaciente} con ${cita.nombreDoctor} creada"
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

    fun actualizarCitaEditada(onResultado: (Boolean) -> Unit) {
        val cita = citaEditando ?: return
        cargando = true
        CitaRepositorio.actualizarCita(
            id = cita.id,
            cita = cita,
            onSuccess = {
                cargarCitas()
                citaEditando = null
                HistorialRepositorio.registrarAccion(
                    tipo = "editar",
                    entidad = "cita",
                    idEntidad = cita.id,
                    descripcion = "Cita de ${cita.nombrePaciente} actualizada"
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

    fun eliminarCita(id: String, onResultado: (Boolean) -> Unit) {
        cargando = true
        CitaRepositorio.eliminarCita(
            id = id,
            onSuccess = {
                cargarCitas()
                HistorialRepositorio.registrarAccion(
                    tipo = "eliminar",
                    entidad = "cita",
                    idEntidad = id,
                    descripcion = "Cita con ID $id eliminada"
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

    fun seleccionarCitaParaEditar(cita: Cita) {
        citaEditando = cita
    }

    fun limpiarEdicion() {
        citaEditando = null
    }
}
