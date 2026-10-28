package com.example.seminario.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.seminario.data.MovieRepository
import com.example.seminario.data.model.Movie
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class MovieUiState {
    object Loading : MovieUiState()
    data class Success(
        val trending: List<Movie>,
        val popularMovies: List<Movie>,
        val popularSeries: List<Movie>,
        val searchResults: List<Movie> = emptyList(),
        val isSearching: Boolean = false
    ) : MovieUiState()
    data class Error(val message: String) : MovieUiState()
}

class MovieViewModel : ViewModel() {
    private val repository = MovieRepository()
    private val _uiState = MutableStateFlow<MovieUiState>(MovieUiState.Loading)
    val uiState: StateFlow<MovieUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // 1. Lista de Películas Guardadas en Favoritos
    private val _favoriteMovies = MutableStateFlow<Set<Movie>>(emptySet())
    val favoriteMovies: StateFlow<Set<Movie>> = _favoriteMovies.asStateFlow()

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
        val currentFavorites = _favoriteMovies.value.toMutableSet()
        if (currentFavorites.any { it.id == movie.id }) {
            currentFavorites.removeAll { it.id == movie.id }
        } else {
            currentFavorites.add(movie)
        }
        _favoriteMovies.value = currentFavorites
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
            } catch (e: Exception) {
                _uiState.value = currentState.copy(searchResults = emptyList(), isSearching = true)
            }
        }
    }

    fun fetchMovies() {
        viewModelScope.launch {
            _uiState.value = MovieUiState.Loading
            try {
                val tendencias = repository.getTrending()
                val peliculas = repository.getPopularMovies()
                val series = repository.getPopularSeries()

                if (tendencias.isNotEmpty() || peliculas.isNotEmpty() || series.isNotEmpty()) {
                    _uiState.value = MovieUiState.Success(
                        trending = tendencias,
                        popularMovies = peliculas,
                        popularSeries = series
                    )
                } else {
                    _uiState.value = MovieUiState.Error("No se encontraron resultados disponibles en este momento.")
                }
            } catch (e: Exception) {
                val errorMessage = e.localizedMessage ?: "Fallo al conectar con el servidor."
                _uiState.value = MovieUiState.Error(errorMessage)
            }
        }
    }
}