package com.example.hospitalapp.modelo

data class HistorialAccion(
    val id: String = "", // ID generado por Firestore
    val tipo: String = "", // "crear", "editar", "eliminar"
    val entidad: String = "", // "paciente", "doctor", "cita"
    val idEntidad: String = "", // ID del paciente, doctor o cita
    val descripcion: String = "", // ejemplo: "Paciente Juan actualizado"
    val usuario: String = "", // correo del usuario que hizo la acción
    val fecha: String = "" // formato ISO 8601 por ejemplo
)
