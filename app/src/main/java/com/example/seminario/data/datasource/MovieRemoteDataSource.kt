package com.example.seminario.data.datasource

import com.example.seminario.data.error.AppError
import com.example.seminario.data.error.AppException
import com.example.seminario.data.network.MovieApiService
import com.example.seminario.data.network.MovieDetailsDto
import com.example.seminario.data.network.MovieDto
import com.example.seminario.di.NetworkModule
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException
import java.io.IOException
import java.util.Locale
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

/**
 * DataSource remoto: único punto de acceso a la API de TMDB.
 * Traduce las excepciones de red/HTTP a un [AppError] tipificado (lanzado como [AppException]).
 * Hilt le inyecta sus dependencias por constructor (provistas por NetworkModule).
 */
@Singleton
class MovieRemoteDataSource @Inject constructor(
    private val apiService: MovieApiService,
    @Named(NetworkModule.TMDB_API_KEY) private val apiKey: String
) {

    // Idioma del dispositivo (ej. "es-AR", "en-US"), para que TMDB responda en ese idioma
    private val language: String
        get() = Locale.getDefault().toLanguageTag()

    suspend fun getPopularMovies(): List<MovieDto> = safeApiCall {
        apiService.getPopularMovies(apiKey, language).results
    }

    suspend fun getTrendingMovies(): List<MovieDto> = safeApiCall {
        apiService.getTrendingMovies(apiKey, language).results
    }

    // Mejor valoradas: mismos errores tipificados que el resto (vía safeApiCall)
    suspend fun getTopRatedMovies(): List<MovieDto> = safeApiCall {
        apiService.getTopRatedMovies(apiKey, language).results
    }

    // Próximos estrenos
    suspend fun getUpcomingMovies(): List<MovieDto> = safeApiCall {
        apiService.getUpcomingMovies(apiKey, language).results
    }

    suspend fun searchMovies(query: String): List<MovieDto> = safeApiCall {
        apiService.searchMovies(apiKey, query, language).results
    }

    suspend fun getMovieDetails(movieId: Int): MovieDetailsDto = safeApiCall {
        apiService.getMovieDetails(movieId, apiKey, language)
    }

    private suspend fun <T> safeApiCall(apiCall: suspend () -> T): T {
        return try {
            apiCall()
        } catch (e: CancellationException) {
            // No se debe "tragar" la cancelación de la corrutina
            throw e
        } catch (e: IOException) {
            // Incluye UnknownHostException (sin internet) y timeouts / conexión cortada
            throw AppException(AppError.NoConnection, e)
        } catch (e: HttpException) {
            val error = when (e.code()) {
                401, 403 -> AppError.Unauthorized
                404 -> AppError.NotFound
                else -> AppError.Server(e.code())
            }
            throw AppException(error, e)
        } catch (e: Exception) {
            throw AppException(AppError.Unexpected, e)
        }
    }
}
