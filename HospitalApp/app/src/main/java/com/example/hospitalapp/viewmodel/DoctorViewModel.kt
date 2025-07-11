package com.example.hospitalapp.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import com.example.hospitalapp.modelo.Doctor
import com.example.hospitalapp.repositorio.DoctorRepositorio
import com.example.hospitalapp.repositorio.HistorialRepositorio

class DoctorViewModel : ViewModel() {

    var doctores: List<Doctor> by mutableStateOf(emptyList())
        private set

    var cargando by mutableStateOf(false)
        private set

    var mensajeError by mutableStateOf<String?>(null)
        private set

    var doctorEditando: Doctor? by mutableStateOf(null)
        private set

    init {
        cargarDoctores()
    }

    fun cargarDoctores() {
        cargando = true
        DoctorRepositorio.obtenerDoctores(
            onSuccess = {
                doctores = it
                cargando = false
            },
            onError = {
                mensajeError = it.message
                cargando = false
            }
        )
    }

    fun agregarDoctor(nuevo: Doctor, onResultado: (Boolean) -> Unit) {
        cargando = true
        DoctorRepositorio.agregarDoctor(
            doctor = nuevo,
            onSuccess = {
                cargarDoctores()
                HistorialRepositorio.registrarAccion(
                    tipo = "crear",
                    entidad = "doctor",
                    idEntidad = nuevo.id,
                    descripcion = "Doctor ${nuevo.nombre} creado"
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

    fun seleccionarDoctorParaEditar(doctor: Doctor) {
        doctorEditando = doctor
    }

    fun actualizarDoctorEditado(onResultado: (Boolean) -> Unit) {
        val doctor = doctorEditando
        if (doctor != null && doctor.id.isNotEmpty()) {
            cargando = true
            DoctorRepositorio.actualizarDoctor(
                id = doctor.id,
                doctor = doctor,
                onSuccess = {
                    doctorEditando = null
                    cargarDoctores()
                    HistorialRepositorio.registrarAccion(
                        tipo = "editar",
                        entidad = "doctor",
                        idEntidad = doctor.id,
                        descripcion = "Doctor ${doctor.nombre} actualizado"
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

    fun eliminarDoctor(id: String, onResultado: (Boolean) -> Unit) {
        cargando = true
        DoctorRepositorio.eliminarDoctor(
            id = id,
            onSuccess = {
                cargarDoctores()
                HistorialRepositorio.registrarAccion(
                    tipo = "eliminar",
                    entidad = "doctor",
                    idEntidad = id,
                    descripcion = "Doctor con ID $id eliminado"
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

    fun limpiarEdicion() {
        doctorEditando = null
    }
}
