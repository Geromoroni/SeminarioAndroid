package com.example.seminario.data.error

/**
 * Tipos de error que puede producir la capa de datos.
 * No contienen texto: la pantalla elige el mensaje (strings.xml) según el tipo,
 * así el error se muestra en el idioma del dispositivo.
 */
sealed class AppError {
    /** Sin conexión a internet o conexión interrumpida. */
    data object NoConnection : AppError()

    /** La API rechazó la API key (HTTP 401 / 403). */
    data object Unauthorized : AppError()

    /** El contenido pedido no existe (HTTP 404). */
    data object NotFound : AppError()

    /** Cualquier otro error HTTP devuelto por el servidor. */
    data class Server(val code: Int) : AppError()

    /** La consulta funcionó pero no devolvió contenido para mostrar. */
    data object Empty : AppError()

    /** Error no previsto (por ejemplo, una respuesta con formato inválido). */
    data object Unexpected : AppError()
}

/** Excepción que transporta un [AppError] desde el DataSource hasta el ViewModel. */
class AppException(val error: AppError, cause: Throwable? = null) : Exception(cause)
