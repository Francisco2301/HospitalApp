package com.example.hospitalapp.repositorio

import com.example.hospitalapp.modelo.Cita
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

object CitaRepositorio {

    private val coleccion = Firebase.firestore.collection("citas")

    fun agregarCita(
        cita: Cita,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        val nuevo = coleccion.document()
        val citaConId = cita.copy(id = nuevo.id)
        nuevo.set(citaConId).addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it) }
    }

    fun obtenerCitas(
        onSuccess: (List<Cita>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        coleccion.get()
            .addOnSuccessListener { resultado ->
                val lista = resultado.documents.mapNotNull { it.toObject(Cita::class.java) }
                onSuccess(lista)
            }
            .addOnFailureListener { onError(it) }
    }

    fun actualizarCita(
        id: String,
        cita: Cita,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        coleccion.document(id).set(cita)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it) }
    }

    fun eliminarCita(
        id: String,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        coleccion.document(id).delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it) }
    }
}
