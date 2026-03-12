# DocVault

DocVault es una aplicación Android diseñada para almacenar documentos personales de forma segura en el dispositivo.

## Descripción
La aplicación permite capturar o seleccionar archivos desde el dispositivo, almacenarlos localmente de forma cifrada y acceder a ellos mediante autenticación biométrica.

El objetivo principal es demostrar buenas prácticas de desarrollo Android moderno, priorizando la seguridad de la información, arquitectura limpia y experiencia de usuario.

## Arquitectura
El proyecto sigue los principios de **Clean Architecture** y **MVVM**. Para más detalles sobre la implementación de la arquitectura, capas y flujo de datos, consulta nuestra documentación detallada:

👉 [Arquitectura de DocVault](documentation/architecture.md)

## Tecnologías y Librerías
- **Kotlin** & **Coroutines/Flow**
- **Jetpack Compose** (UI)
- **Hilt** (Inyección de dependencias)
- **Room** (Persistencia local)
- **KSP**
- **Biometric API**
- **Architecture Components** (ViewModel, Lifecycle)

## Estructura del Proyecto
- `:app`: Punto de entrada de la aplicación y UI principal.
- `:domain`: Lógica de negocio y modelos de dominio (Puro Kotlin).
- `:data`: Implementación de repositorios y fuentes de datos (Room, File System).
- `:core`: Utilidades transversales y seguridad.
- `:design`: Sistema de diseño y componentes UI reutilizables.
