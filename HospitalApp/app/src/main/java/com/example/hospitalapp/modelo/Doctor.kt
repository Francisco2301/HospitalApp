package com.example.hospitalapp.modelo

data class Doctor(
    val id: String = "",               // ID generado por Firestore
    val nombre: String = "",
    val especialidad: String = "",
    val telefono: String = "",
    val correo: String = ""
)
