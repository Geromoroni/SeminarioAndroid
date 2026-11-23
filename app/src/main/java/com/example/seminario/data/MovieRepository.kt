package com.example.seminario.data

import com.example.seminario.data.datasource.MovieRemoteDataSource
import com.example.seminario.data.model.Movie
import com.example.seminario.data.model.MovieDetails
import com.example.seminario.data.network.MovieDetailsDto
import com.example.seminario.data.network.MovieDto
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository: obtiene los datos del DataSource y los transforma a modelos de dominio.
 * Los errores llegan ya tipificados desde el DataSource (AppException con un AppError).
 */
@Singleton
class MovieRepository @Inject constructor(
    private val remoteDataSource: MovieRemoteDataSource
) {

    suspend fun getPopularMovies(): List<Movie> =
        remoteDataSource.getPopularMovies().map { it.toMovie() }

    suspend fun getTrendingMovies(): List<Movie> =
        remoteDataSource.getTrendingMovies().map { it.toMovie() }

    // Mejor valoradas, mapeadas a modelo de dominio
    suspend fun getTopRatedMovies(): List<Movie> =
        remoteDataSource.getTopRatedMovies().map { it.toMovie() }

    // Próximos estrenos, mapeados a modelo de dominio
    suspend fun getUpcomingMovies(): List<Movie> =
        remoteDataSource.getUpcomingMovies().map { it.toMovie() }

    suspend fun searchMovies(query: String): List<Movie> =
        remoteDataSource.searchMovies(query).map { it.toMovie() }

    suspend fun getMovieDetails(movieId: Int): MovieDetails =
        remoteDataSource.getMovieDetails(movieId).toMovieDetails()

    private fun MovieDto.toMovie(): Movie = Movie(
        id = id,
        title = title.orEmpty(),
        synopsis = overview.orEmpty(),
        rating = voteAverage ?: 0.0,
        year = releaseYear(releaseDate),
        imageUrl = posterUrl(posterPath),
        backdropUrl = backdropUrl(backdropPath)
    )

    private fun MovieDetailsDto.toMovieDetails(): MovieDetails = MovieDetails(
        id = id,
        title = title.orEmpty(),
        synopsis = overview.orEmpty(),
        rating = voteAverage ?: 0.0,
        year = releaseYear(releaseDate),
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

    private fun releaseYear(date: String?): String? = date?.take(4)?.ifEmpty { null }
    private fun posterUrl(path: String?) = if (path != null) "$IMAGE_BASE_URL/w500$path" else ""
    private fun backdropUrl(path: String?) = if (path != null) "$IMAGE_BASE_URL/w780$path" else ""

    private companion object {
        const val IMAGE_BASE_URL = "https://image.tmdb.org/t/p"
        const val MAX_CAST = 10
    }
}
