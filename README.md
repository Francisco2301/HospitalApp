# 🏥 HospitalApp - Sistema de Gestión Hospitalaria

Bienvenido a **HospitalApp**, una aplicación móvil desarrollada con **Kotlin** y **Jetpack Compose**, diseñada para gestionar pacientes, doctores, citas médicas y roles de acceso en un entorno hospitalario moderno.

## 🚀 Funcionalidades principales

- Autenticación con Google y modo visitante
- Sistema de roles (admin, superadmin, doctor, asistente, paciente, visitante)
- CRUD completo de:
  - Pacientes
  - Doctores
  - Citas médicas
- Gestión de administradores (solo para superadmin)
- Historial de accesos y acciones en tiempo real
- Validaciones dinámicas y navegación protegida por roles

## 🔒 Roles y permisos

| Rol        | Acceso                                 | Permisos CRUD                            |
|------------|-----------------------------------------|-------------------------------------------|
| Superadmin | Todo el sistema                         | CRUD completo de todo                     |
| Admin      | Pacientes, Doctores, Citas              | CRUD de doctores, pacientes y citas       |
| Asistente  | Pacientes, Doctores, Citas              | CRUD de pacientes y citas (solo editar)   |
| Doctor     | Pacientes, Citas                        | CRUD de citas, ver pacientes              |
| Paciente   | Doctores, Citas                         | Solo visualización                        |
| Visitante  | Doctores                                | Solo visualización                        |

## 🛠️ Tecnologías utilizadas

- Kotlin + Jetpack Compose
- Firebase Auth & Firestore
- MVVM Architecture
- Material 3 Design
- Android SDK 33+

## 📦 Estructura del proyecto
HospitalApp/
├── app/
│ ├── ui/ # Pantallas y componentes
│ ├── modelo/ # Entidades (Paciente, Doctor, etc.)
│ ├── repositorio/ # Acceso a Firestore
│ ├── util/ # Utilidades y control de roles
│ └── viewmodel/ # Lógica MVVM
├── build.gradle.kts
└── settings.gradle.kts


## 🔐 Acceso seguro

Los roles están protegidos mediante Firestore y solo usuarios autorizados pueden acceder a funciones sensibles como gestionar administradores o eliminar datos.

## 🧪 ¿Cómo probarlo?

1. Clonar el repositorio:
   ```bash
   git clone https://github.com/Francisco2301/HospitalApp.git
2. Abrir únicamente la carpeta que diga HospitalApp (sin el 1 u otra versión duplicada) desde Android Studio.

3. Asegurarse de que esté dentro la carpeta app, los archivos build.gradle.kts y settings.gradle.kts.

4. Agregar el archivo google-services.json dentro de la carpeta /app (si no está).

5. Ejecutar en un emulador o dispositivo físico.

👨‍💻 Desarrolladores
José Francisco López
Nelson Lacayo
Carlos Avalos
Max Martinez

📧 jlopezm@uamv.edu.ni

🏫 Universidad Americana (UAM)

Este proyecto fue desarrollado como parte de un sistema de gestión hospitalaria académico, con potencial de uso real.
