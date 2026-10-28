package com.example.seminario.data.network

import com.google.gson.annotations.SerializedName

// Esta clase representa la respuesta completa de la API, que contiene una lista llamada "results"
data class MovieResponse(
    @SerializedName("results") val results: List<MovieDto>
)

data class MovieDto(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String?, // Puede ser nulo si es una serie
    @SerializedName("name") val name: String?,   // Para las series
    @SerializedName("overview") val overview: String?,
    @SerializedName("vote_average") val voteAverage: Double?,
    @SerializedName("release_date") val releaseDate: String?,
    @SerializedName("first_air_date") val firstAirDate: String?,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("backdrop_path") val backdropPath: String?
)