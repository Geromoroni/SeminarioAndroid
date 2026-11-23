# App de Películas - Seminario Android

Aplicación Android desarrollada con **Kotlin** y **Jetpack Compose** que consume la API de **The Movie Database (TMDB)** aplicando el patrón de arquitectura **MVVM**.

## 🚀 Características
- **Arquitectura:** MVVM en capas: `ApiService (Retrofit)` → `RemoteDataSource` → `Repository` → `ViewModel` → UI, con estado expuesto mediante `StateFlow`.
- **Inyección de dependencias:** Hilt (`@HiltAndroidApp`, `@AndroidEntryPoint`, `@HiltViewModel`, `NetworkModule`).
- **Detalle de película:** pantalla con ViewModel propio que recibe solo el ID por navegación (`SavedStateHandle`) y consulta el endpoint `movie/{movie_id}` (con `append_to_response=credits`) para mostrar géneros, duración, dirección y reparto.
- **Errores tipificados:** el DataSource traduce las excepciones de red/HTTP a una `sealed class AppError` (sin conexión, no autorizado, no encontrado, error del servidor, inesperado) y la pantalla elige el mensaje desde `strings.xml`.
- **Red:** Retrofit + Gson.
- **Carga de imágenes:** Coil (`AsyncImage`).
- **Navegación:** Jetpack Compose Navigation (pantalla principal y detalle).
- **Búsqueda:** buscador en tiempo real con transición a vista en cuadrícula.
- **Desafíos implementados:**
  - Idioma dinámico: la app (strings) y las respuestas de la API siguen el idioma del dispositivo (español / inglés).
  - Integración nativa para compartir películas (Android Intent).

## 🛠️ Configuración previa para ejecutar
1. Obtener una clave gratuita en [The Movie Database (TMDB)](https://www.themoviedb.org/).
2. Agregar la clave en el archivo `local.properties` de la raíz del proyecto (no se sube al repositorio):
   ```properties
   TMDB_API_KEY=tu_api_key
   ```
3. Sincronizar Gradle y ejecutar. La clave llega al código a través de `BuildConfig.TMDB_API_KEY`.
