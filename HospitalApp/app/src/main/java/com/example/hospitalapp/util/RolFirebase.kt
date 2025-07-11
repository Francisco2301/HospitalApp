package com.example.hospitalapp.util

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.runtime.mutableStateListOf

object RolFirebase {

    private val firestore = FirebaseFirestore.getInstance()
    private val listaAdmins = mutableStateListOf<String>()

    // ✅ Cargar todos los administradores desde Firebase (usado al iniciar app)
    fun cargarAdmins(onCompleto: () -> Unit = {}) {
        firestore.collection("roles")
            .get()
            .addOnSuccessListener { result ->
                listaAdmins.clear()
                result.documents.forEach { doc ->
                    val rol = doc.getString("rol")
                    val correo = doc.id
                    if (rol == "admin" || rol == "superadmin") {
                        listaAdmins.add(correo)
                    }
                }
                onCompleto()
            }
            .addOnFailureListener {
                onCompleto()
            }
    }

    // ✅ Verifica si el usuario actual es admin o superadmin
    fun esAdminActual(): Boolean {
        val correoActual = FirebaseAuth.getInstance().currentUser?.email ?: return false
        return listaAdmins.contains(correoActual)
    }

    // ✅ Verifica si el usuario actual es superadmin
    fun esSuperadmin(callback: (Boolean) -> Unit) {
        val correo = FirebaseAuth.getInstance().currentUser?.email ?: return callback(false)
        firestore.collection("roles").document(correo)
            .get()
            .addOnSuccessListener { doc ->
                callback(doc.getString("rol") == "superadmin")
            }
            .addOnFailureListener {
                callback(false)
            }
    }

    // ✅ Obtener el rol de cualquier usuario
    fun obtenerRolUsuarioActual(callback: (String) -> Unit) {
        val correo = FirebaseAuth.getInstance().currentUser?.email ?: return callback("ninguno")

        firestore.collection("roles")
            .document(correo)
            .get()
            .addOnSuccessListener { doc ->
                val rol = doc.getString("rol") ?: "ninguno"
                callback(rol)
            }
            .addOnFailureListener {
                callback("ninguno")
            }
    }

    // ✅ Agrega o actualiza un rol
    fun asignarRol(correo: String, rol: String, onResultado: (Boolean) -> Unit) {
        if (correo.isBlank() || rol.isBlank()) {
            onResultado(false)
            return
        }

        val data = mapOf("rol" to rol)
        firestore.collection("roles")
            .document(correo)
            .set(data)
            .addOnSuccessListener {
                if (rol == "admin" || rol == "superadmin") {
                    if (!listaAdmins.contains(correo)) {
                        listaAdmins.add(correo)
                    }
                } else {
                    listaAdmins.remove(correo)
                }
                onResultado(true)
            }
            .addOnFailureListener { onResultado(false) }
    }

    // ✅ Elimina completamente un usuario del sistema
    fun eliminarUsuario(correo: String, onResultado: (Boolean) -> Unit) {
        firestore.collection("roles")
            .document(correo)
            .delete()
            .addOnSuccessListener {
                listaAdmins.remove(correo)
                onResultado(true)
            }
            .addOnFailureListener { onResultado(false) }
    }

    // ✅ Devuelve todos los usuarios con su rol
    fun cargarTodosLosUsuarios(callback: (List<Pair<String, String>>) -> Unit) {
        firestore.collection("roles")
            .get()
            .addOnSuccessListener { result ->
                val lista = result.documents.mapNotNull {
                    val correo = it.id
                    val rol = it.getString("rol") ?: return@mapNotNull null
                    correo to rol
                }
                callback(lista)
            }
            .addOnFailureListener {
                callback(emptyList())
            }
    }
}
