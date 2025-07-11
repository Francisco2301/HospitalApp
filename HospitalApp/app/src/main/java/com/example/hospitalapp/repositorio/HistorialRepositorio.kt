package com.example.hospitalapp.repositorio

import com.example.hospitalapp.modelo.HistorialAccion
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

object HistorialRepositorio {

    private val db = FirebaseFirestore.getInstance()

    fun registrarAccion(tipo: String, entidad: String, idEntidad: String, descripcion: String) {
        val correo = FirebaseAuth.getInstance().currentUser?.email ?: "desconocido"
        val fecha = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        val accion = HistorialAccion(
            tipo = tipo,
            entidad = entidad,
            idEntidad = idEntidad,
            descripcion = descripcion,
            usuario = correo,
            fecha = fecha
        )

        db.collection("historial")
            .add(accion)
    }
}
