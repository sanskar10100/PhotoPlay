package dev.sanskar.photoplay.ui.watchlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import dagger.hilt.android.AndroidEntryPoint
import dev.sanskar.photoplay.R
import dev.sanskar.photoplay.ui.theme.PhotoPlayTheme
import dev.sanskar.photoplay.util.UiState

@AndroidEntryPoint
class WatchlistFragment : Fragment() {
    private val viewModel by viewModels<WatchlistViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                PhotoPlayTheme {
                    Scaffold(
                        floatingActionButton = {
                            FloatingActionButton(onClick = { viewModel.addSample() }) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = null,
                                        modifier = Modifier.padding(start = 4.dp)
                                    )
                                    Text(
                                        text = "Add Watchlist",
                                        style = MaterialTheme.typography.h3,
                                        modifier = Modifier.padding(4.dp)
                                    )
                                }
                            }
                        }
                    ) {
                        WatchlistScreen(Modifier.padding(it))
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalLifecycleComposeApi::class, ExperimentalAnimationApi::class)
    @Composable
    fun WatchlistScreen(modifier: Modifier = Modifier) {
        val state by viewModel.watchlists.collectAsStateWithLifecycle()
        AnimatedContent(targetState = state) { state ->
            when (state) {
                is UiState.Loading -> {}
                is UiState.Error -> {}
                is UiState.Empty -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = modifier.fillMaxSize()
                    ) {
                        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.empty))
                        LottieAnimation(
                            composition = composition,
                            modifier = Modifier
                                .fillMaxSize(0.7f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Create a watchlist to get started!",
                            style = MaterialTheme.typography.h2,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                is UiState.Success -> {
                    LazyColumn(
                        modifier = modifier
                    ) {
                        items(state.data) { watchlist ->
                            Text(watchlist.name)
                        }
                    }
                }
            }
        }
    }


}