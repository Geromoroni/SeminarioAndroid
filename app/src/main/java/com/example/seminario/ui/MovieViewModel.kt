package com.example.seminario.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.seminario.data.FavoritesRepository
import com.example.seminario.data.MovieRepository
import com.example.seminario.data.error.AppError
import com.example.seminario.data.error.AppException
import com.example.seminario.data.model.Movie
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class MovieUiState {
    data object Loading : MovieUiState()
    data class Success(
        val trending: List<Movie>,
        val popularMovies: List<Movie>,
        // Carruseles extra: si fallan quedan vacíos (no bloquean la pantalla)
        val topRated: List<Movie> = emptyList(),
        val upcoming: List<Movie> = emptyList(),
        val searchResults: List<Movie> = emptyList(),
        val isSearching: Boolean = false
    ) : MovieUiState()
    data class Error(val error: AppError) : MovieUiState()
}

// ViewModel de la pantalla principal (lista de películas). Hilt inyecta los repositorios.
@HiltViewModel
class MovieViewModel @Inject constructor(
    private val repository: MovieRepository,
    private val favoritesRepository: FavoritesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<MovieUiState>(MovieUiState.Loading)
    val uiState: StateFlow<MovieUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // 1. Lista de Películas Guardadas en Favoritos (compartida a través del repositorio)
    val favoriteMovies: StateFlow<List<Movie>> = favoritesRepository.favorites

    // 2. Control de pestaña seleccionada en la barra inferior ("home", "search", "list", "settings")
    private val _selectedTab = MutableStateFlow("home")
    val selectedTab: StateFlow<String> = _selectedTab.asStateFlow()

    init {
        fetchMovies()
    }

    fun selectTab(tab: String) {
        _selectedTab.value = tab
    }

    fun toggleFavorite(movie: Movie) {
        favoritesRepository.toggleFavorite(movie)
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query

        val currentState = _uiState.value
        if (currentState is MovieUiState.Success) {
            if (query.trim().length > 2) {
                _selectedTab.value = "search"
                performSearch(query.trim(), currentState)
            } else {
                _uiState.value = currentState.copy(isSearching = false, searchResults = emptyList())
                if (query.trim().isEmpty() && _selectedTab.value == "search") {
                    _selectedTab.value = "home"
                }
            }
        }
    }

    private fun performSearch(query: String, currentState: MovieUiState.Success) {
        viewModelScope.launch {
            _uiState.value = currentState.copy(isSearching = true)

            try {
                val results = repository.searchMovies(query)
                _uiState.value = currentState.copy(searchResults = results, isSearching = true)
            } catch (e: AppException) {
                _uiState.value = currentState.copy(searchResults = emptyList(), isSearching = true)
            }
        }
    }

    fun fetchMovies() {
        viewModelScope.launch {
            _uiState.value = MovieUiState.Loading
            try {
                val tendencias = repository.getTrendingMovies()
                val peliculas = repository.getPopularMovies()

                _uiState.value = if (tendencias.isNotEmpty() || peliculas.isNotEmpty()) {
                    MovieUiState.Success(
                        trending = tendencias,
                        popularMovies = peliculas,
                        // Las listas nuevas no deciden el error: si fallan, quedan vacías
                        topRated = loadOrEmpty { repository.getTopRatedMovies() },
                        upcoming = loadOrEmpty { repository.getUpcomingMovies() }
                    )
                } else {
                    MovieUiState.Error(AppError.Empty)
                }
            } catch (e: AppException) {
                _uiState.value = MovieUiState.Error(e.error)
            }
        }
    }

    // Ejecuta la carga y, si hay un error de la API, devuelve una lista vacía
    private suspend fun loadOrEmpty(load: suspend () -> List<Movie>): List<Movie> =
        try {
            load()
        } catch (e: AppException) {
            emptyList()
        }
}
