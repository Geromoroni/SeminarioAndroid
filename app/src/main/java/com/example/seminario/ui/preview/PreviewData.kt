package com.example.seminario.ui.preview

import com.example.seminario.data.model.Movie
import com.example.seminario.data.model.MovieDetails

// Datos de ejemplo usados SOLO por los @Preview de Compose (la app real usa la API)

internal val previewMovies = listOf(
    Movie(
        id = 1,
        title = "Spider-Man: Brand New Day",
        synopsis = "A forgotten Peter Parker lives alone as a full-time Spider-Man...",
        rating = 8.0,
        year = "2026",
        imageUrl = ""
    ),
    Movie(
        id = 2,
        title = "Stranger Things",
        synopsis = "Cuando un niño desaparece, sus amigos y familia inician una búsqueda...",
        rating = 8.7,
        year = "2022",
        imageUrl = ""
    )
)

internal val previewMovieDetails = MovieDetails(
    id = 1,
    title = "Spider-Man: Brand New Day",
    synopsis = "A forgotten Peter Parker lives alone as a full-time Spider-Man...",
    rating = 8.0,
    year = "2026",
    runtimeMinutes = 145,
    posterUrl = "",
    backdropUrl = "",
    homepage = null,
    genres = listOf("Acción", "Aventura"),
    directors = listOf("Destin Daniel Cretton"),
    cast = listOf("Tom Holland", "Zendaya")
)
