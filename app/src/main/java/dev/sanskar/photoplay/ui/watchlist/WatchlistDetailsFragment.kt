package dev.sanskar.photoplay.ui.watchlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.compose.AsyncImage
import dagger.hilt.android.AndroidEntryPoint
import dev.sanskar.photoplay.R
import dev.sanskar.photoplay.db.Watchlist
import dev.sanskar.photoplay.ui.composables.LottieEmpty
import dev.sanskar.photoplay.ui.theme.PhotoPlayTheme
import dev.sanskar.photoplay.ui.theme.MontserratFontFamily
import dev.sanskar.photoplay.util.UiState
import dev.sanskar.photoplay.util.clickWithRipple
import dev.sanskar.photoplay.util.getDownloadUrl

@AndroidEntryPoint
class WatchlistDetailsFragment : Fragment() {
    private val viewModel by viewModels<WatchlistViewModel>()
    private val navArgs by navArgs<WatchlistDetailsFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel.getMoviesForWatchlist(navArgs.watchlistId)
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                PhotoPlayTheme {
                    WatchlistDetails()
                }
            }
        }
    }

    @OptIn(ExperimentalLifecycleComposeApi::class, ExperimentalAnimationApi::class)
    @Composable
    fun WatchlistDetails(modifier: Modifier = Modifier) {
        val state by viewModel.watchlistDetails.collectAsStateWithLifecycle()
        AnimatedContent(targetState = state) { state ->
            when (state) {
                is UiState.Loading -> {}
                is UiState.Error -> {}
                is UiState.Empty -> {
                    Column(
                        modifier = modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        LottieEmpty("Add some movies to get started!")
                        Spacer(Modifier.height(16.dp))
                        OutlinedButton(onClick = { findNavController().navigate(R.id.action_watchlistDetailsFragment_to_homeFragment) }) {
                            Text("Go To Movies")
                        }
                    }
                }
                is UiState.Success -> {
                    LazyColumn {
                        item {
                            WatchlistHeader(watchlist = state.data.first)
                        }
                        items(state.data.second) { movie ->
                            Card(
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.padding(horizontal = 8.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    AsyncImage(
                                        model = movie.posterPath?.getDownloadUrl() ?: movie.backdropPath?.getDownloadUrl() ?:"",
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(128.dp),
                                        contentScale = ContentScale.Crop
                                    )
                                    Box(
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        Text(
                                            text = movie.name,
                                            style = MaterialTheme.typography.h2,
                                            modifier = Modifier.padding(16.dp)
                                        )
                                        Icon(
                                            imageVector = Icons.Filled.Cancel,
                                            contentDescription = "Remove from watchlist",
                                            modifier = Modifier
                                                .align(Alignment.BottomEnd)
                                                .clickWithRipple {
                                                    viewModel.removeMovieFromWatchlist(movie.id, navArgs.watchlistId)
                                                }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun WatchlistHeader(watchlist: Watchlist) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = watchlist.name,
                    style = MaterialTheme.typography.h1.copy(fontFamily = MontserratFontFamily),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
                Text(
                    text = watchlist.description,
                    style = MaterialTheme.typography.body1,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                Text(
                    text = "Created on: ${watchlist.createdOn}",
                    style = MaterialTheme.typography.caption,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }

}