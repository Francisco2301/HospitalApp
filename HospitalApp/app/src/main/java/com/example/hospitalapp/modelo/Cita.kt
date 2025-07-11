package com.example.hospitalapp.modelo

data class Cita(
    val id: String = "",
    val idDoctor: String = "",
    val nombreDoctor: String = "", // ✅ asegurado
    val idPaciente: String = "",
    val nombrePaciente: String = "", // ✅ asegurado
    val fecha: String = "",
    val hora: String = "" // ✅ asegurado
)
