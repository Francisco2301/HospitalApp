package com.example.hospitalapp.repositorio

import com.example.hospitalapp.modelo.Paciente
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

object PacienteRepositorio {
    private val coleccion = Firebase.firestore.collection("pacientes")

    fun agregarPaciente(
        paciente: Paciente,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        val usuario = FirebaseAuth.getInstance().currentUser
        val uid = usuario?.uid ?: "desconocido"
        val correo = usuario?.email ?: "correo_desconocido"

        val datos = mapOf(
            "nombre" to paciente.nombre,
            "edad" to paciente.edad,
            "telefono" to paciente.telefono,
            "creadoPorUid" to uid,
            "creadoPorEmail" to correo
        )

        coleccion
            .add(datos)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onError(e) }
    }

    fun obtenerPacientes(
        onSuccess: (List<Paciente>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        coleccion
            .get()
            .addOnSuccessListener { snapshot ->
                val lista = snapshot.documents.mapNotNull { doc ->
                    val data = doc.data
                    if (data != null) {
                        Paciente(
                            id = doc.id,
                            nombre = data["nombre"] as? String ?: "",
                            edad = (data["edad"] as? Long)?.toInt() ?: 0,
                            telefono = data["telefono"] as? String ?: ""
                        )
                    } else null
                }
                onSuccess(lista)
            }
            .addOnFailureListener { e -> onError(e) }
    }

    fun actualizarPaciente(
        id: String,
        paciente: Paciente,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        val datos = mapOf(
            "nombre" to paciente.nombre,
            "edad" to paciente.edad,
            "telefono" to paciente.telefono
        )

        coleccion
            .document(id)
            .set(datos)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onError(e) }
    }

    fun eliminarPaciente(
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
