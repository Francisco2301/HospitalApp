package com.example.hospitalapp.ui.pantalla

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.hospitalapp.R
import com.google.firebase.auth.FirebaseAuth

@Composable
fun PantallaMenuPrincipal(
    onIrPacientes: () -> Unit,
    onIrDoctores: () -> Unit,
    onIrCitas: () -> Unit,
    onIrGestionAdmins: () -> Unit,
    onIrHistorial: () -> Unit, // ✅ agregado
    esAdmin: Boolean,
    esSuperAdmin: Boolean,
    rol: String,
    onCerrarSesion: () -> Unit
) {
    val correo = FirebaseAuth.getInstance().currentUser?.email ?: "sesión activa"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Imagen de bienvenida
        Image(
            painter = painterResource(id = R.drawable.hospital),
            contentDescription = "Icono Hospital",
            modifier = Modifier
                .size(180.dp)
                .padding(bottom = 24.dp)
        )

        Text(
            text = "Bienvenido al Sistema Hospitalario",
            style = MaterialTheme.typography.headlineSmall
        )

        Text(
            text = "Rol actual: $rol",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 4.dp)
        )

        Text(
            text = "Correo: $correo",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (rol in listOf("admin", "asistente", "superadmin", "doctor")) {
            Button(onClick = onIrPacientes, modifier = Modifier.fillMaxWidth()) {
                Text("Ver Pacientes")
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (rol in listOf("admin", "asistente", "superadmin", "paciente", "ninguno")) {
            Button(onClick = onIrDoctores, modifier = Modifier.fillMaxWidth()) {
                Text("Ver Doctores")
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (rol in listOf("admin", "asistente", "superadmin", "doctor", "paciente")) {
            Button(onClick = onIrCitas, modifier = Modifier.fillMaxWidth()) {
                Text("Ver Citas")
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (rol == "superadmin") {
            Button(onClick = onIrGestionAdmins, modifier = Modifier.fillMaxWidth()) {
                Text("Gestionar Administradores")
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (rol == "admin" || rol == "superadmin") {
            Button(onClick = onIrHistorial, modifier = Modifier.fillMaxWidth()) {
                Text("Historial de Acciones")
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onCerrarSesion, modifier = Modifier.fillMaxWidth()) {
            Text("Cerrar sesión ($correo)")
        }
    }
}
