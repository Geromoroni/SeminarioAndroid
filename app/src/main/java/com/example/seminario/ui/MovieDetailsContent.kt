package com.example.seminario.ui

import android.content.Intent
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.seminario.R
import com.example.seminario.data.model.MovieDetails
import com.example.seminario.data.model.mockMovieDetails
import com.example.seminario.ui.navigation.ErrorScreen
import com.example.seminario.ui.navigation.LoadingScreen
import java.util.Locale

// Pantalla de detalle con estado: carga, error con reintento y contenido
@Composable
fun MovieDetailScreen(
    viewModel: MovieDetailViewModel,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val isFavorite by viewModel.isFavorite.collectAsState()

    when (val state = uiState) {
        is MovieDetailUiState.Loading -> LoadingScreen()
        is MovieDetailUiState.Error -> ErrorScreen(
            message = state.message,
            onRetry = { viewModel.loadMovieDetails() }
        )
        is MovieDetailUiState.Success -> MovieDetailsContent(
            details = state.details,
            isFavorite = isFavorite,
            onToggleFavorite = { viewModel.toggleFavorite() },
            onBackClick = onBackClick
        )
    }
}

@Composable
fun MovieDetailsContent(
    details: MovieDetails,
    isFavorite: Boolean = false,
    onToggleFavorite: () -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val shareChooserTitle = stringResource(R.string.share_chooser_title)
    val shareMessageTemplate = stringResource(
        R.string.share_message_template,
        details.title,
        details.synopsis,
        "https://www.themoviedb.org/movie/${details.id}"
    )

    val infoParts = listOfNotNull(
        details.runtimeMinutes?.let { stringResource(R.string.details_runtime, it) },
        details.year,
        "★ " + String.format(Locale.getDefault(), "%.1f", details.rating)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F0F13))
            .verticalScroll(rememberScrollState())
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(450.dp)
                .background(Color(0xFF1A1A1F))
        ) {
            val imageToLoad = details.backdropUrl.ifEmpty { details.posterUrl }
            if (imageToLoad.isNotEmpty()) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(imageToLoad)
                        .crossfade(true)
                        .build(),
                    contentDescription = details.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color(0xFF0F0F13)),
                            startY = 600f
                        )
                    )
            )

            Text(
                text = details.title,
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(start = 16.dp, end = 16.dp, bottom = 32.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 48.dp, start = 16.dp, end = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.background(Color.Black.copy(alpha = 0.5f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = stringResource(R.string.cd_back),
                        tint = Color.White
                    )
                }

                IconButton(
                    onClick = {
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, shareMessageTemplate)
                            type = "text/plain"
                        }
                        val shareIntent = Intent.createChooser(sendIntent, shareChooserTitle)
                        context.startActivity(shareIntent)
                    },
                    modifier = Modifier.background(Color.Black.copy(alpha = 0.5f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = stringResource(R.string.cd_share),
                        tint = Color.White
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = infoParts.joinToString("  ·  "),
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.weight(1f))

            // Ícono interactivo de Favoritos (Corazón)
            IconButton(onClick = onToggleFavorite) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = stringResource(R.string.cd_favorite),
                    tint = if (isFavorite) Color(0xFFE53935) else Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Text(
            text = details.synopsis.ifEmpty { stringResource(R.string.details_no_synopsis) },
            color = Color(0xFFB3B3B3),
            fontSize = 16.sp,
            lineHeight = 24.sp,
            modifier = Modifier.padding(16.dp)
        )

        if (details.genres.isNotEmpty()) {
            SectionTitle(stringResource(R.string.section_genres))
            ChipRow(details.genres)
        }

        if (details.directors.isNotEmpty()) {
            SectionTitle(stringResource(R.string.section_director))
            ChipRow(details.directors)
        }

        if (details.cast.isNotEmpty()) {
            SectionTitle(stringResource(R.string.section_cast))
            ChipRow(details.cast)
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun ChipRow(values: List<String>) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(values) { value ->
            DetailChip(value)
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        color = Color(0xFF808080),
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(start = 16.dp, top = 24.dp, bottom = 8.dp)
    )
}

@Composable
fun DetailChip(text: String) {
    Box(
        modifier = Modifier
            .background(Color(0xFF25252B), shape = RoundedCornerShape(50))
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(text = text, color = Color.White, fontSize = 14.sp)
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMovieDetails() {
    MovieDetailsContent(details = mockMovieDetails, onBackClick = {})
}
