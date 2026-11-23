package com.example.seminario.data.model

// Datos mínimos de una película para mostrarla en listas (populares, búsqueda, favoritos)
data class Movie(
    val id: Int,
    val title: String,
    val synopsis: String,
    val rating: Double,
    val year: String?,
    val imageUrl: String,
    val backdropUrl: String = ""
)
