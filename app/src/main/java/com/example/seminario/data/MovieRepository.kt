package com.example.seminario.data

import com.example.seminario.data.datasource.MovieRemoteDataSource
import com.example.seminario.data.model.Movie
import com.example.seminario.data.model.MovieDetails
import com.example.seminario.data.network.MovieDetailsDto
import com.example.seminario.data.network.MovieDto
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException
import java.io.IOException
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository: obtiene los datos del DataSource, los transforma a modelos de dominio
 * y traduce los errores a mensajes entendibles para el usuario.
 */
@Singleton
class MovieRepository @Inject constructor(
    private val remoteDataSource: MovieRemoteDataSource
) {

    suspend fun getPopularMovies(): List<Movie> = safeApiCall {
        remoteDataSource.getPopularMovies().map { it.toMovie() }
    }

    suspend fun getTrendingMovies(): List<Movie> = safeApiCall {
        remoteDataSource.getTrendingMovies().map { it.toMovie() }
    }

    suspend fun searchMovies(query: String): List<Movie> = safeApiCall {
        remoteDataSource.searchMovies(query).map { it.toMovie() }
    }

    suspend fun getMovieDetails(movieId: Int): MovieDetails = safeApiCall {
        remoteDataSource.getMovieDetails(movieId).toMovieDetails()
    }

    private fun MovieDto.toMovie(): Movie = Movie(
        id = id,
        title = title ?: "Sin título",
        synopsis = overview ?: "",
        rating = voteAverage ?: 0.0,
        year = releaseDate?.take(4)?.ifEmpty { null } ?: "N/A",
        imageUrl = posterUrl(posterPath),
        backdropUrl = backdropUrl(backdropPath)
    )

    private fun MovieDetailsDto.toMovieDetails(): MovieDetails = MovieDetails(
        id = id,
        title = title ?: "Sin título",
        synopsis = overview ?: "",
        rating = voteAverage ?: 0.0,
        year = releaseDate?.take(4)?.ifEmpty { null } ?: "N/A",
        runtimeMinutes = runtime?.takeIf { it > 0 },
        posterUrl = posterUrl(posterPath),
        backdropUrl = backdropUrl(backdropPath),
        homepage = homepage?.takeIf { it.isNotBlank() },
        genres = genres.orEmpty().map { it.name },
        directors = credits?.crew.orEmpty().filter { it.job == "Director" }.map { it.name },
        cast = credits?.cast.orEmpty()
            .sortedBy { it.order ?: Int.MAX_VALUE }
            .take(MAX_CAST)
            .map { it.name }
    )

    private fun posterUrl(path: String?) = if (path != null) "$IMAGE_BASE_URL/w500$path" else ""
    private fun backdropUrl(path: String?) = if (path != null) "$IMAGE_BASE_URL/w780$path" else ""

    private suspend fun <T> safeApiCall(apiCall: suspend () -> T): T {
        return try {
            apiCall()
        } catch (e: CancellationException) {
            // No se debe "tragar" la cancelación de la corrutina
            throw e
        } catch (e: UnknownHostException) {
            throw IOException("Sin conexión a internet. Verifica tu red.", e)
        } catch (e: IOException) {
            throw IOException("Error de red o conexión interrumpida.", e)
        } catch (e: HttpException) {
            val mensaje = when (e.code()) {
                401, 403 -> "Error de autenticación con el servidor de películas."
                404 -> "El contenido solicitado no fue encontrado en TMDB."
                in 500..599 -> "El servidor de TMDB no está respondiendo. Inténtalo más tarde."
                else -> "Error en el servidor (${e.code()}). Inténtalo de nuevo."
            }
            throw Exception(mensaje, e)
        } catch (e: Exception) {
            throw Exception("Ocurrió un error inesperado: ${e.localizedMessage ?: "Consulte con soporte."}", e)
        }
    }

    private companion object {
        const val IMAGE_BASE_URL = "https://image.tmdb.org/t/p"
        const val MAX_CAST = 10
    }
}
