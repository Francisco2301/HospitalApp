package com.example.hospitalapp.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore

class AdminViewModel : ViewModel() {

    var usuariosConRol by mutableStateOf<List<Pair<String, String>>>(emptyList())
        private set

    var usuarioEditando by mutableStateOf<Pair<String, String>?>(null)
        private set

    var cargando by mutableStateOf(false)
        private set

    var mensajeError by mutableStateOf<String?>(null)
        private set

    private val db = FirebaseFirestore.getInstance()
    private val coleccion = db.collection("roles")

    init {
        cargarUsuarios()
    }

    fun cargarUsuarios() {
        cargando = true
        mensajeError = null
        coleccion.get()
            .addOnSuccessListener { result ->
                usuariosConRol = result.documents.mapNotNull {
                    val correo = it.id
                    val rol = it.getString("rol")
                    if (rol != null) correo to rol else null
                }
                cargando = false
            }
            .addOnFailureListener { e ->
                mensajeError = e.message
                cargando = false
            }
    }

    fun agregarOActualizarUsuario(correo: String, rol: String, onResultado: (Boolean, String) -> Unit) {
        if (correo.isBlank() || rol.isBlank()) {
            onResultado(false, "Correo y rol no pueden estar vacíos.")
            return
        }

        if (rol !in listOf("admin", "doctor", "asistente", "paciente")) {
            onResultado(false, "Rol inválido. Debe ser: admin, doctor, asistente o paciente.")
            return
        }

        cargando = true
        coleccion.document(correo)
            .set(mapOf("rol" to rol))
            .addOnSuccessListener {
                cargarUsuarios()
                usuarioEditando = null
                cargando = false
                onResultado(true, "Usuario registrado/actualizado.")
            }
            .addOnFailureListener {
                cargando = false
                onResultado(false, "Error al guardar: ${it.message}")
            }
    }

    fun eliminarUsuario(correo: String, onResultado: (Boolean, String) -> Unit) {
        cargando = true
        coleccion.document(correo)
            .delete()
            .addOnSuccessListener {
                cargarUsuarios()
                cargando = false
                onResultado(true, "Usuario eliminado.")
            }
            .addOnFailureListener {
                cargando = false
                onResultado(false, "Error al eliminar: ${it.message}")
            }
    }

    fun seleccionarParaEditar(usuario: Pair<String, String>) {
        usuarioEditando = usuario
    }

    fun cancelarEdicion() {
        usuarioEditando = null
    }
}
