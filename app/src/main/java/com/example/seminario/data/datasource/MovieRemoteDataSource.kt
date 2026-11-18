package com.example.seminario.data.datasource

import com.example.seminario.data.network.MovieApiService
import com.example.seminario.data.network.MovieDetailsDto
import com.example.seminario.data.network.MovieDto
import com.example.seminario.di.NetworkModule
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

/**
 * DataSource remoto: único punto de acceso a la API de TMDB.
 * Hilt le inyecta sus dependencias por constructor (provistas por NetworkModule).
 */
@Singleton
class MovieRemoteDataSource @Inject constructor(
    private val apiService: MovieApiService,
    @Named(NetworkModule.TMDB_API_KEY) private val apiKey: String
) {

    suspend fun getPopularMovies(): List<MovieDto> =
        apiService.getPopularMovies(apiKey).results

    suspend fun getTrendingMovies(): List<MovieDto> =
        apiService.getTrendingMovies(apiKey).results

    suspend fun searchMovies(query: String): List<MovieDto> =
        apiService.searchMovies(apiKey, query).results

    suspend fun getMovieDetails(movieId: Int): MovieDetailsDto =
        apiService.getMovieDetails(movieId, apiKey)
}
