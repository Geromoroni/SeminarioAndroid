# App de Películas - Seminario Android

Aplicación Android desarrollada con **Kotlin** y **Jetpack Compose** que consume la API de **The Movie Database (TMDB)** aplicando el patrón de arquitectura **MVVM**.

## 🚀 Características
- **Arquitectura:** MVVM con Repository Pattern y gestión de estado mediante `StateFlow`.
- **Red:** Retrofit + Gson con manejo defensivo de excepciones de conectividad.
- **Carga de imágenes:** Integración con Coil (`AsyncImage`).
- **Navegación:** Jetpack Compose Navigation (Pantalla principal y Detalles).
- **Búsqueda:** Buscador en tiempo real con transición a vista en cuadrícula.
- **Desafíos implementados:**
  - Idioma dinámico (respuestas de la API en español).
  - Integración nativa para compartir películas (Android Intent).

## 🛠️ Configuración previa para ejecutar
1. Obtener una clave gratuita en [The Movie Database (TMDB)](https://www.themoviedb.org/).
2. Abrir `MovieRepository.kt` y colocar la clave en la variable `apiKey`.
