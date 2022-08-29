package dev.sanskar.photoplay.ui.detail

import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Theaters
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.compose.AsyncImage
import dagger.hilt.android.AndroidEntryPoint
import dev.sanskar.photoplay.data.Movie
import dev.sanskar.photoplay.data.MovieDetails
import dev.sanskar.photoplay.data.asMovie
import dev.sanskar.photoplay.ui.composables.AddMovieToWatchLists
import dev.sanskar.photoplay.ui.composables.ErrorDialog
import dev.sanskar.photoplay.ui.composables.ProgressBar
import dev.sanskar.photoplay.ui.theme.MontserratFontFamily
import dev.sanskar.photoplay.ui.theme.PhotoPlayTheme
import dev.sanskar.photoplay.util.UiState
import dev.sanskar.photoplay.util.clickWithRipple
import dev.sanskar.photoplay.util.getDownloadUrl

@AndroidEntryPoint
class DetailFragment : Fragment() {
    val viewModel by viewModels<DetailViewModel>()
    val navArgs by navArgs<DetailFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel.getMovieDetails(navArgs.movieId)
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                PhotoPlayTheme {
                    DetailScreen()
                }
            }
        }
    }

    @OptIn(ExperimentalAnimationApi::class)
    @Composable
    fun DetailScreen() {
        if (viewModel.showAddMovieToWatchlistDialog) {
            AddMovieToWatchLists(
                movie = viewModel.movieWithWatchlistInclusionStatus.movie,
                checklist = viewModel.movieWithWatchlistInclusionStatus.watchlistInclusionStatus,
                onWatchlistCreate = { title, description ->
                    viewModel.addWatchlist(title, description)
                }
            ) {
                viewModel.updateWatchlistInclusionsForMovie(it,
                    viewModel.movieWithWatchlistInclusionStatus.movie)
            }
        }

        AnimatedContent(targetState = viewModel.movieDetails) { state ->
            when (state) {
                is UiState.Loading -> {
                    ProgressBar(loading = true)
                }
                is UiState.Empty -> {}
                is UiState.Error -> {
                    ErrorDialog(message = "Error: ${state.message}") {
                        findNavController().popBackStack()
                    }
                }
                is UiState.Success -> {
                    Column(
                        horizontalAlignment = CenterHorizontally
                    ) {
                        AsyncImage(
                            model = state.data.backdrop_path?.getDownloadUrl() ?: state.data.poster_path?.getDownloadUrl() ?: "",
                            contentDescription = "Movie Poster",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(240.dp)
                                .padding(8.dp)
                                .clip(RoundedCornerShape(16.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Text(
                            text = state.data.title,
                            style = MaterialTheme.typography.h2,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.align(CenterHorizontally)
                        )
                        Spacer(Modifier.height(8.dp))
                        LazyRow {
                            items(state.data.genres) { genre ->
                                Text(
                                    text = genre.name,
                                    style = MaterialTheme.typography.subtitle2.copy(fontFamily = MontserratFontFamily)
                                )
                                if (genre != state.data.genres.last()) {
                                    Text(
                                        text = ", ",
                                        style = MaterialTheme.typography.subtitle2.copy(fontFamily = MontserratFontFamily)
                                    )
                                }
                            }
                        }
                        AddToWatchlistButton(state.data)
                        Text(
                            text = state.data.tagline,
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.h6.copy(fontFamily = MontserratFontFamily),
                            modifier = Modifier.padding(4.dp),
                        )
                        Text(
                            text = state.data.overview,
                            textAlign = TextAlign.Justify,
                            modifier = Modifier.padding(4.dp)
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun AddToWatchlistButton(movie: MovieDetails, modifier: Modifier = Modifier) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Divider()
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(
                    Modifier
                        .weight(1f)
                        .clickWithRipple { viewModel.getMovieWithWatchlistInclusionStatus(movie.asMovie()) }
                        .padding(vertical = 16.dp),
                    horizontalAlignment = CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Theaters,
                        contentDescription = null,
                        tint = MaterialTheme.colors.primaryVariant
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Add To Watchlist",
                        style = MaterialTheme.typography.h3,
                        color = MaterialTheme.colors.primaryVariant
                    )
                }

                Column(
                    Modifier
                        .weight(1f)
                        .clickWithRipple {
                            startActivity(Intent(Intent.ACTION_VIEW,
                                "https://imdb.com/title/${movie.imdb_id}".toUri()))
                        }
                        .padding(vertical = 16.dp),
                    horizontalAlignment = CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.OpenInNew,
                        contentDescription = null,
                        tint = MaterialTheme.colors.primaryVariant
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "View on IMDB",
                        style = MaterialTheme.typography.h3,
                        color = MaterialTheme.colors.primaryVariant
                    )
                }
            }
            Divider()
        }
    }
}