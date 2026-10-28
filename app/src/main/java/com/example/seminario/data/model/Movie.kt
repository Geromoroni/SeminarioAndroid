package com.example.seminario.data.model

data class Movie(
    val id: Int,
    val title: String,
    val synopsis: String,
    val rating: Double,
    val year: String,
    val duration: String,
    val imageUrl: String,
    val backdropUrl: String = ""
)

val mockMovies = listOf(
    Movie(
        id = 1,
        title = "Spider-Man: Brand New Day",
        synopsis = "A forgotten Peter Parker lives alone as a full-time Spider-Man...",
        rating = 8.0,
        year = "2026",
        duration = "145 min",
        imageUrl = "",
        backdropUrl = ""
    ),
    Movie(
        id = 2,
        title = "Stranger Things",
        synopsis = "Cuando un niño desaparece, sus amigos y familia inician una búsqueda...",
        rating = 8.7,
        year = "2022",
        duration = "50 min",
        imageUrl = "",
        backdropUrl = ""
    )
)