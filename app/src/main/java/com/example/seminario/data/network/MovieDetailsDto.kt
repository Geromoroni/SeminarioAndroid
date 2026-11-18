package com.example.seminario.data.network

import com.google.gson.annotations.SerializedName

// Respuesta del endpoint movie/{movie_id} (con append_to_response=credits)
data class MovieDetailsDto(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String?,
    @SerializedName("overview") val overview: String?,
    @SerializedName("vote_average") val voteAverage: Double?,
    @SerializedName("release_date") val releaseDate: String?,
    @SerializedName("runtime") val runtime: Int?,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("backdrop_path") val backdropPath: String?,
    @SerializedName("homepage") val homepage: String?,
    @SerializedName("genres") val genres: List<GenreDto>?,
    @SerializedName("credits") val credits: CreditsDto?
)

data class GenreDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String
)

data class CreditsDto(
    @SerializedName("cast") val cast: List<CastDto>?,
    @SerializedName("crew") val crew: List<CrewDto>?
)

data class CastDto(
    @SerializedName("name") val name: String,
    @SerializedName("order") val order: Int?
)

data class CrewDto(
    @SerializedName("name") val name: String,
    @SerializedName("job") val job: String?
)
