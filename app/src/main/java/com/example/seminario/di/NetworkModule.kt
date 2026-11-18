package com.example.seminario.di

import com.example.seminario.BuildConfig
import com.example.seminario.data.network.MovieApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

/**
 * Módulo de Hilt que provee las dependencias de red, que no podemos anotar con @Inject
 * porque son clases de librerías externas (Retrofit) o valores de configuración.
 * El resto (DataSource, Repositories, ViewModels) se inyecta por constructor con @Inject.
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL = "https://api.themoviedb.org/3/"
    const val TMDB_API_KEY = "tmdb_api_key"

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideMovieApiService(retrofit: Retrofit): MovieApiService =
        retrofit.create(MovieApiService::class.java)

    // La key viene de local.properties a través de BuildConfig (ver app/build.gradle.kts)
    @Provides
    @Named(TMDB_API_KEY)
    fun provideApiKey(): String = BuildConfig.TMDB_API_KEY
}
