package com.example.seminario.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.seminario.R
import com.example.seminario.data.model.Movie
import com.example.seminario.data.model.mockMovies

@Composable
fun MovieContent(
    trending: List<Movie> = emptyList(),
    popularMovies: List<Movie> = emptyList(),
    popularSeries: List<Movie> = emptyList(),
    movies: List<Movie> = emptyList(),
    favoriteMovies: List<Movie> = emptyList(),
    searchQuery: String = "",
    isSearching: Boolean = false,
    searchResults: List<Movie> = emptyList(),
    selectedTab: String = "home",
    onSearchQueryChanged: (String) -> Unit = {},
    onTabSelected: (String) -> Unit = {},
    onToggleFavorite: (Movie) -> Unit = {},
    onNavigateToDetails: (Int) -> Unit = {}
) {
    val actualTrending = trending.ifEmpty { movies }
    val actualPopularMovies = popularMovies.ifEmpty { movies }
    val actualPopularSeries = popularSeries.ifEmpty { movies }

    val focusRequester = remember { FocusRequester() }

    Scaffold(
        containerColor = Color(0xFF0F0F13),
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 48.dp, bottom = 16.dp, start = 16.dp, end = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = stringResource(R.string.cd_logo),
                    tint = Color(0xFF2962FF),
                    modifier = Modifier.size(32.dp)
                )

                Spacer(modifier = Modifier.width(16.dp))

                BasicTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChanged,
                    textStyle = TextStyle(color = Color.White, fontSize = 14.sp),
                    cursorBrush = SolidColor(Color(0xFF2962FF)),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .focusRequester(focusRequester)
                        .background(Color(0xFF1A1A1F), shape = RoundedCornerShape(8.dp)),
                    decorationBox = { innerTextField ->
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(modifier = Modifier.weight(1f)) {
                                if (searchQuery.isEmpty()) {
                                    Text(
                                        text = stringResource(R.string.search_placeholder),
                                        color = Color.Gray,
                                        fontSize = 14.sp
                                    )
                                }
                                innerTextField()
                            }
                            if (searchQuery.isNotEmpty()) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = stringResource(R.string.cd_clear),
                                    tint = Color.Gray,
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clickable { onSearchQueryChanged("") }
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = stringResource(R.string.cd_search),
                                    tint = Color.Gray
                                )
                            }
                        }
                    }
                )
            }
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0F0F13).copy(alpha = 0.65f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { }
                    .padding(bottom = 24.dp, top = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BottomBarItem(
                    icon = Icons.Default.Home,
                    text = stringResource(R.string.nav_home),
                    isSelected = selectedTab == "home" && !isSearching,
                    onClick = {
                        onTabSelected("home")
                        onSearchQueryChanged("")
                    }
                )
                BottomBarItem(
                    icon = Icons.Default.Search,
                    text = stringResource(R.string.nav_search),
                    isSelected = selectedTab == "search" || isSearching,
                    onClick = {
                        onTabSelected("search")
                        focusRequester.requestFocus()
                    }
                )
                BottomBarItem(
                    icon = Icons.Default.List,
                    text = stringResource(R.string.nav_list),
                    isSelected = selectedTab == "list",
                    onClick = {
                        onTabSelected("list")
                        onSearchQueryChanged("")
                    }
                )
                BottomBarItem(
                    icon = Icons.Default.Settings,
                    text = stringResource(R.string.nav_settings),
                    isSelected = selectedTab == "settings",
                    onClick = { onTabSelected("settings") }
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding())
        ) {
            when {
                // 1. PESTAÑA "LISTA DE DESEADOS" / FAVORITOS
                selectedTab == "list" -> {
                    if (favoriteMovies.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FavoriteBorder,
                                    contentDescription = null,
                                    tint = Color.Gray,
                                    modifier = Modifier.size(64.dp)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = stringResource(R.string.empty_favorites_message),
                                    color = Color.Gray,
                                    fontSize = 16.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            contentPadding = PaddingValues(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(favoriteMovies) { movie ->
                                MovieItem(movie = movie, onClick = { onNavigateToDetails(movie.id) })
                            }
                        }
                    }
                }

                // 2. MODO BÚSQUEDA
                isSearching -> {
                    if (searchResults.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.TopCenter
                        ) {
                            Text(
                                text = stringResource(R.string.no_results_for_query, searchQuery),
                                color = Color.Gray,
                                modifier = Modifier.padding(top = 32.dp)
                            )
                        }
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            contentPadding = PaddingValues(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(searchResults) { movie ->
                                MovieItem(movie = movie, onClick = { onNavigateToDetails(movie.id) })
                            }
                        }
                    }
                }

                // 3. PESTAÑA PRINCIPAL (HOME)
                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        if (actualTrending.isNotEmpty()) {
                            MovieCarousel(
                                title = stringResource(R.string.carousel_trending),
                                movies = actualTrending,
                                onMovieClick = { onNavigateToDetails(it.id) }
                            )
                        }

                        if (actualPopularMovies.isNotEmpty()) {
                            MovieCarousel(
                                title = stringResource(R.string.carousel_popular_movies),
                                movies = actualPopularMovies,
                                onMovieClick = { onNavigateToDetails(it.id) }
                            )
                        }

                        if (actualPopularSeries.isNotEmpty()) {
                            MovieCarousel(
                                title = stringResource(R.string.carousel_popular_series),
                                movies = actualPopularSeries,
                                onMovieClick = { onNavigateToDetails(it.id) }
                            )
                        }

                        Spacer(modifier = Modifier.height(100.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun MovieCarousel(
    title: String,
    movies: List<Movie>,
    onMovieClick: (Movie) -> Unit = {}
) {
    Column(modifier = Modifier.padding(bottom = 24.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = stringResource(R.string.see_all),
                color = Color.Gray,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(movies) { movie ->
                MovieItem(movie = movie, onClick = { onMovieClick(movie) })
            }
        }
    }
}

@Composable
fun BottomBarItem(
    icon: ImageVector,
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit = {}
) {
    val iconTint = if (isSelected) Color.White else Color.White.copy(alpha = 0.55f)
    val textColor = if (isSelected) Color.White else Color.White.copy(alpha = 0.55f)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            tint = iconTint,
            modifier = Modifier.size(if (isSelected) 28.dp else 24.dp)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = text,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0F0F13)
@Composable
fun PreviewMovieContent() {
    val listaLarga = listOf(mockMovies[0], mockMovies[1], mockMovies[0], mockMovies[1])
    MovieContent(
        trending = listaLarga,
        popularMovies = listaLarga,
        popularSeries = listaLarga,
        onNavigateToDetails = { /* No hacemos nada en la vista previa */ }
    )
}