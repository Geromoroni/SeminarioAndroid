package com.example.seminario.data

import com.example.seminario.data.model.Movie
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Lista de deseados (en memoria). Es @Singleton: Hilt inyecta la misma instancia en
 * ambos ViewModels, así la pantalla principal y la de detalle comparten los datos
 * sin compartir ViewModel.
 */
@Singleton
class FavoritesRepository @Inject constructor() {

    private val _favorites = MutableStateFlow<List<Movie>>(emptyList())
    val favorites: StateFlow<List<Movie>> = _favorites.asStateFlow()

    fun toggleFavorite(movie: Movie) {
        _favorites.update { current ->
            if (current.any { it.id == movie.id }) {
                current.filterNot { it.id == movie.id }
            } else {
                current + movie
            }
        }
    }
}
