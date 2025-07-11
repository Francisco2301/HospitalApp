package com.example.hospitalapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.hospitalapp.ui.login.PantallaLoginGoogle
import com.example.hospitalapp.ui.pantalla.*
import com.example.hospitalapp.ui.theme.HospitalAppTheme
import com.example.hospitalapp.util.RolFirebase
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            HospitalAppTheme {
                var usuario by remember { mutableStateOf(FirebaseAuth.getInstance().currentUser) }
                var vista by remember { mutableStateOf("login") }

                var cargandoRoles by remember { mutableStateOf(true) }
                var rolUsuario by remember { mutableStateOf("ninguno") }
                var esAdmin by remember { mutableStateOf(false) }
                var esSuperAdmin by remember { mutableStateOf(false) }

                val correo = usuario?.email

                // Cargar roles desde Firebase cuando haya usuario
                LaunchedEffect(usuario) {
                    if (usuario != null) {
                        RolFirebase.obtenerRolUsuarioActual { rol ->
                            rolUsuario = rol
                            esAdmin = rol == "admin"
                            esSuperAdmin = rol == "superadmin"
                            cargandoRoles = false
                        }
                    } else {
                        cargandoRoles = false
                    }
                }

                fun cerrarSesion() {
                    FirebaseAuth.getInstance().signOut()
                    GoogleSignIn.getClient(
                        this,
                        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
                    ).signOut()
                    usuario = null
                    vista = "login"
                    rolUsuario = "ninguno"
                    esAdmin = false
                    esSuperAdmin = false
                }

                if (usuario != null && vista == "login") {
                    if (rolUsuario == "ninguno") {
                        Toast.makeText(this, "Acceso no autorizado para $correo", Toast.LENGTH_LONG).show()
                        cerrarSesion()
                    } else {
                        vista = "menu"
                    }
                }

                if (cargandoRoles) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                    when (vista) {
                        "login" -> PantallaLoginGoogle(
                            onLoginExitoso = {
                                usuario = FirebaseAuth.getInstance().currentUser
                                vista = "menu"
                            },
                            onModoInvitado = {
                                usuario = null
                                vista = "menu"
                                rolUsuario = "ninguno"
                                esAdmin = false
                                esSuperAdmin = false
                            }
                        )

                        "menu" -> PantallaMenuPrincipal(
                            onIrPacientes = { vista = "pacientes" },
                            onIrDoctores = { vista = "doctores" },
                            onIrCitas = { vista = "citas" },
                            onIrGestionAdmins = { vista = "admins" },
                            onIrHistorial = { vista = "historial" }, // ✅ agregado
                            esAdmin = esAdmin,
                            esSuperAdmin = esSuperAdmin,
                            rol = rolUsuario,
                            onCerrarSesion = { cerrarSesion() }
                        )

                        "pacientes" -> PantallaPacientes(
                            onVolver = { vista = "menu" },
                            rolUsuario = rolUsuario
                        )

                        "doctores" -> PantallaDoctores(
                            onVolver = { vista = "menu" },
                            rolUsuario = rolUsuario
                        )

                        "citas" -> PantallaCita(
                            onVolver = { vista = "menu" },
                            rolUsuario = rolUsuario
                        )

                        "admins" -> PantallaGestionAdmins(
                            onVolver = { vista = "menu" }
                        )

                        "historial" -> PantallaHistorialAcciones(
                            rol = rolUsuario,
                            onVolver = { vista = "menu" }
                        )
                    }
                }
            }
        }
    }
}
