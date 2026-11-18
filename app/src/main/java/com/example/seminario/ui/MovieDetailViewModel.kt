package com.example.seminario.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.seminario.data.FavoritesRepository
import com.example.seminario.data.MovieRepository
import com.example.seminario.data.model.MovieDetails
import com.example.seminario.data.model.toMovie
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class MovieDetailUiState {
    object Loading : MovieDetailUiState()
    data class Success(val details: MovieDetails) : MovieDetailUiState()
    data class Error(val message: String) : MovieDetailUiState()
}

/**
 * ViewModel propio de la pantalla de detalle.
 * Solo recibe el ID de la película (argumento de navegación, vía SavedStateHandle)
 * y consulta el servicio movie-details a través del repositorio.
 * Hilt inyecta el SavedStateHandle y los repositorios.
 */
@HiltViewModel
class MovieDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: MovieRepository,
    private val favoritesRepository: FavoritesRepository
) : ViewModel() {

    private val movieId: Int = checkNotNull(savedStateHandle[MOVIE_ID_ARG]) {
        "Falta el argumento $MOVIE_ID_ARG"
    }

    private val _uiState = MutableStateFlow<MovieDetailUiState>(MovieDetailUiState.Loading)
    val uiState: StateFlow<MovieDetailUiState> = _uiState.asStateFlow()

    val isFavorite: StateFlow<Boolean> = favoritesRepository.favorites
        .map { favorites -> favorites.any { it.id == movieId } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    init {
        loadMovieDetails()
    }

    fun loadMovieDetails() {
        viewModelScope.launch {
            _uiState.value = MovieDetailUiState.Loading
            _uiState.value = try {
                MovieDetailUiState.Success(repository.getMovieDetails(movieId))
            } catch (e: Exception) {
                MovieDetailUiState.Error(e.localizedMessage ?: "No se pudo cargar el detalle de la película.")
            }
        }
    }

    fun toggleFavorite() {
        val state = _uiState.value as? MovieDetailUiState.Success ?: return
        favoritesRepository.toggleFavorite(state.details.toMovie())
    }

    companion object {
        const val MOVIE_ID_ARG = "movieId"
    }
}
