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
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.seminario.R
import com.example.seminario.data.model.Movie
import com.example.seminario.data.model.mockMovies

@Composable
fun MovieDetailsContent(
    movie: Movie,
    isFavorite: Boolean = false,
    onToggleFavorite: (Movie) -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val shareChooserTitle = stringResource(R.string.share_chooser_title)
    val shareMessageTemplate = stringResource(
        R.string.share_message_template,
        movie.title,
        movie.synopsis,
        "https://www.themoviedb.org/movie/${movie.id}"
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
            val imageToLoad = movie.backdropUrl.ifEmpty { movie.imageUrl }
            if (imageToLoad.isNotEmpty()) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(imageToLoad)
                        .crossfade(true)
                        .build(),
                    contentDescription = movie.title,
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
                text = movie.title,
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp)
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

                Row {
                    IconButton(
                        onClick = {
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, shareMessageTemplate)
                                type = "text/plain"
                            }
                            val shareIntent = Intent.createChooser(sendIntent, shareChooserTitle)
                            context.startActivity(shareIntent)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = stringResource(R.string.cd_share),
                            tint = Color.White
                        )
                    }
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
                text = "${movie.duration}  ·  ${movie.year}  ·  ${movie.rating}",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.weight(1f))

            // Ícono interactivo de Favoritos (Corazón)
            IconButton(onClick = { onToggleFavorite(movie) }) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = stringResource(R.string.cd_favorite),
                    tint = if (isFavorite) Color(0xFFE53935) else Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Text(
            text = movie.synopsis,
            color = Color(0xFFB3B3B3),
            fontSize = 16.sp,
            lineHeight = 24.sp,
            modifier = Modifier.padding(16.dp)
        )

        Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            DetailChip(stringResource(R.string.genre_action))
            Spacer(modifier = Modifier.width(8.dp))
            DetailChip(stringResource(R.string.genre_adventure))
        }

        SectionTitle(stringResource(R.string.section_director))
        Row(modifier = Modifier.padding(horizontal = 16.dp)) {
            DetailChip("Destin Daniel Cretton")
        }

        SectionTitle(stringResource(R.string.section_cast))
        LazyRow(contentPadding = PaddingValues(horizontal = 16.dp)) {
            val cast = listOf("Tom Holland", "Zendaya", "Mark Ruffalo")
            items(cast) { actor ->
                DetailChip(actor)
                Spacer(modifier = Modifier.width(8.dp))
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
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
    MovieDetailsContent(movie = mockMovies.first(), onBackClick = {})
}