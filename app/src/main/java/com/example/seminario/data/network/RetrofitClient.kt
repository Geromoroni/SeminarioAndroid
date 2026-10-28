package com.example.seminario.data.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // La dirección principal de la API
    private const val BASE_URL = "https://api.themoviedb.org/3/"

    // Creamos la instancia de Retrofit (solo se crea una vez gracias a 'by lazy')
    val apiService: MovieApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()) // Le pasamos nuestro traductor Gson
            .build()
            .create(MovieApiService::class.java)
    }
}