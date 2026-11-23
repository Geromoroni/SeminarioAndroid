package com.example.seminario.data.model

// Datos completos de una película, obtenidos del endpoint movie-details
data class MovieDetails(
    val id: Int,
    val title: String,
    val synopsis: String,
    val rating: Double,
    val year: String?,
    val runtimeMinutes: Int?,
    val posterUrl: String,
    val backdropUrl: String,
    val homepage: String?,
    val genres: List<String>,
    val directors: List<String>,
    val cast: List<String>
)

// Datos mínimos para guardar la película en la lista de deseados
fun MovieDetails.toMovie(): Movie = Movie(
    id = id,
    title = title,
    synopsis = synopsis,
    rating = rating,
    year = year,
    imageUrl = posterUrl,
    backdropUrl = backdropUrl
)
