package com.example.hospitalapp.util

object PermisosCorreo {

    // Mapa que asocia correos a roles
    private val permisos = mapOf(
        "jlopezm@uamv.edu.ni" to "admin",       // ✅ admin
        "jereyesr@uamv.edu.ni" to "admin",      // ✅ admin
        "lector@uamv.edu.ni" to "lector",       // ejemplo lector
        "especial@uamv.edu.ni" to "especial"    // otro ejemplo
    )

    // Devuelve el rol asignado a un correo. Si no está, retorna "ninguno".
    fun obtenerRol(correo: String?): String {
        return permisos[correo] ?: "ninguno"
    }

    // Permite insertar, actualizar o eliminar
    fun puedeEditar(correo: String?): Boolean {
        return obtenerRol(correo) == "admin"
    }

    // Permite ver datos (lectura)
    fun puedeLeer(correo: String?): Boolean {
        val rol = obtenerRol(correo)
        return rol == "admin" || rol == "lector"
    }

    // Permite acceso general a la app
    fun esPermitido(correo: String?): Boolean {
        return obtenerRol(correo) != "ninguno"
    }
}
