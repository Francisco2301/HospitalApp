package com.example.hospitalapp.repositorio

import com.example.hospitalapp.modelo.Doctor
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

object DoctorRepositorio {
    private val coleccion = Firebase.firestore.collection("doctores")

    fun agregarDoctor(
        doctor: Doctor,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        val datos = mapOf(
            "nombre" to doctor.nombre,
            "especialidad" to doctor.especialidad,
            "telefono" to doctor.telefono,
            "correo" to doctor.correo
        )

        coleccion
            .add(datos)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onError(e) }
    }

    fun obtenerDoctores(
        onSuccess: (List<Doctor>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        coleccion
            .get()
            .addOnSuccessListener { snapshot ->
                val lista = snapshot.documents.mapNotNull { doc ->
                    val data = doc.data
                    if (data != null) {
                        Doctor(
                            id = doc.id,
                            nombre = data["nombre"] as? String ?: "",
                            especialidad = data["especialidad"] as? String ?: "",
                            telefono = data["telefono"] as? String ?: "",
                            correo = data["correo"] as? String ?: ""
                        )
                    } else null
                }
                onSuccess(lista)
            }
            .addOnFailureListener { e -> onError(e) }
    }

    fun actualizarDoctor(
        id: String,
        doctor: Doctor,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        val datos = mapOf(
            "nombre" to doctor.nombre,
            "especialidad" to doctor.especialidad,
            "telefono" to doctor.telefono,
            "correo" to doctor.correo
        )

        coleccion
            .document(id)
            .set(datos)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onError(e) }
    }

    fun eliminarDoctor(
        id: String,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        coleccion
            .document(id)
            .delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onError(e) }
    }
}
