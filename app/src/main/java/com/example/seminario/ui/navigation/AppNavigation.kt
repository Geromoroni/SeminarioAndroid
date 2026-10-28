package com.example.seminario.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.seminario.R
import com.example.seminario.ui.MovieContent
import com.example.seminario.ui.MovieDetailsContent
import com.example.seminario.ui.MovieUiState
import com.example.seminario.ui.MovieViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val viewModel: MovieViewModel = viewModel()

    val favoriteMovies by viewModel.favoriteMovies.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()

    NavHost(navController = navController, startDestination = "home") {

        composable("home") {
            val uiState by viewModel.uiState.collectAsState()
            val searchQuery by viewModel.searchQuery.collectAsState()

            when (val state = uiState) {
                is MovieUiState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFF0F0F13)),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFF2962FF))
                    }
                }
                is MovieUiState.Error -> {
                    ErrorScreen(
                        message = state.message,
                        onRetry = { viewModel.fetchMovies() }
                    )
                }
                is MovieUiState.Success -> {
                    MovieContent(
                        trending = state.trending,
                        popularMovies = state.popularMovies,
                        popularSeries = state.popularSeries,
                        favoriteMovies = favoriteMovies.toList(),
                        searchQuery = searchQuery,
                        isSearching = state.isSearching,
                        searchResults = state.searchResults,
                        selectedTab = selectedTab,
                        onSearchQueryChanged = { viewModel.onSearchQueryChanged(it) },
                        onTabSelected = { viewModel.selectTab(it) },
                        onToggleFavorite = { viewModel.toggleFavorite(it) },
                        onNavigateToDetails = { movieId ->
                            navController.navigate("details/$movieId")
                        }
                    )
                }
            }
        }

        composable(
            route = "details/{movieId}",
            arguments = listOf(navArgument("movieId") { type = NavType.IntType })
        ) { backStackEntry ->
            val movieId = backStackEntry.arguments?.getInt("movieId") ?: 0
            val uiState by viewModel.uiState.collectAsState()

            when (val state = uiState) {
                is MovieUiState.Success -> {
                    val peliculaSeleccionada = state.trending.find { it.id == movieId }
                        ?: state.popularMovies.find { it.id == movieId }
                        ?: state.popularSeries.find { it.id == movieId }
                        ?: state.searchResults.find { it.id == movieId }
                        ?: favoriteMovies.find { it.id == movieId }

                    if (peliculaSeleccionada != null) {
                        MovieDetailsContent(
                            movie = peliculaSeleccionada,
                            isFavorite = favoriteMovies.any { it.id == movieId },
                            onToggleFavorite = { viewModel.toggleFavorite(it) },
                            onBackClick = { navController.popBackStack() }
                        )
                    } else {
                        ErrorScreen(
                            message = stringResource(R.string.error_movie_not_found),
                            onRetry = { navController.popBackStack() }
                        )
                    }
                }
                is MovieUiState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFF0F0F13)),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFF2962FF))
                    }
                }
                is MovieUiState.Error -> {
                    ErrorScreen(
                        message = state.message,
                        onRetry = { viewModel.fetchMovies() }
                    )
                }
            }
        }
    }
}

@Composable
fun ErrorScreen(
    message: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F0F13))
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = stringResource(R.string.cd_error),
            tint = Color(0xFF2962FF),
            modifier = Modifier.size(64.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.error_title),
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = message,
            color = Color(0xFFA0A0A5),
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onRetry,
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2962FF))
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.btn_retry),
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}