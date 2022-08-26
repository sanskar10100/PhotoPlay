package dev.sanskar.photoplay.ui.composables

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import dev.sanskar.photoplay.data.Movie
import dev.sanskar.photoplay.util.clickWithRipple
import dev.sanskar.photoplay.util.getDownloadUrl

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MoviesGrid(
    movies: List<Movie>,
    modifier: Modifier = Modifier,
    onTap: (Int) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(128.dp),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
    ) {
        items(movies, key = {
            it.id
        }) { movie ->
            Surface(
                elevation = 3.dp,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.animateItemPlacement()
                    .clickWithRipple { onTap(movie.id) }
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AsyncImage(
                        model = movie.poster_path?.getDownloadUrl() ?: movie.backdrop_path?.getDownloadUrl() ?: "",
                        contentDescription = null,
                        contentScale = ContentScale.FillBounds,
                        modifier = Modifier.clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)),
                    )
                    Text(
                        text = movie.title,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.h3,
                        modifier = Modifier.padding(8.dp),
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}