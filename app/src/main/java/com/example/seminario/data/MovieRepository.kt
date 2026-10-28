package com.example.seminario.data

import com.example.seminario.data.model.Movie
import com.example.seminario.data.network.MovieDto
import com.example.seminario.data.network.RetrofitClient
import retrofit2.HttpException
import java.io.IOException
import java.net.UnknownHostException

class MovieRepository {

    private val apiKey = "b03dd5922357d959cbb9c22994c60c2a"

    private fun mapToMovie(dto: MovieDto): Movie {
        val poster = if (dto.posterPath != null) "https://image.tmdb.org/t/p/w500${dto.posterPath}" else ""
        val backdrop = if (dto.backdropPath != null) "https://image.tmdb.org/t/p/w780${dto.backdropPath}" else ""

        return Movie(
            id = dto.id,
            title = dto.title ?: dto.name ?: "Sin título",
            synopsis = dto.overview ?: "",
            rating = dto.voteAverage ?: 0.0,
            year = (dto.releaseDate ?: dto.firstAirDate)?.take(4) ?: "N/A",
            duration = "120 min",
            imageUrl = poster,
            backdropUrl = backdrop
        )
    }

    private suspend fun <T> safeApiCall(apiCall: suspend () -> T): T {
        return try {
            apiCall()
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

    suspend fun getPopularMovies(): List<Movie> = safeApiCall {
        RetrofitClient.apiService.getPopularMovies(apiKey).results.map { mapToMovie(it) }
    }

    suspend fun getPopularSeries(): List<Movie> = safeApiCall {
        RetrofitClient.apiService.getPopularSeries(apiKey).results.map { mapToMovie(it) }
    }

    suspend fun getTrending(): List<Movie> = safeApiCall {
        RetrofitClient.apiService.getTrending(apiKey).results.map { mapToMovie(it) }
    }

    suspend fun searchMovies(query: String): List<Movie> = safeApiCall {
        RetrofitClient.apiService.searchMovies(apiKey, query).results.map { mapToMovie(it) }
    }
}