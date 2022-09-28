package dev.sanskar.photoplay.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
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
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import dagger.hilt.android.AndroidEntryPoint
import dev.sanskar.photoplay.R
import dev.sanskar.photoplay.ui.composables.MoviesGrid
import dev.sanskar.photoplay.ui.composables.ProgressBar
import dev.sanskar.photoplay.ui.theme.PhotoPlayTheme
import dev.sanskar.photoplay.util.UiState

@AndroidEntryPoint
class SearchResultFragment : Fragment() {
    private val viewModel by viewModels<SearchViewModel>()
    private val navArgs by navArgs<SearchResultFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel.search(navArgs.query)
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                PhotoPlayTheme {
                    SearchScreen()
                }
            }
        }
    }

    @OptIn(ExperimentalLifecycleComposeApi::class)
    @Composable
    fun SearchScreen() {
        val state by viewModel.searchResult.collectAsStateWithLifecycle()
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            SearchBar()

            when (val state = state) {
                is UiState.Loading -> {
                    ProgressBar(true)
                }
                is UiState.Empty -> {
                    LottieAnimation(
                        composition = rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.empty)).value,
                        iterations = LottieConstants.IterateForever,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "No results found, are you sure you typed the right thing?",
                        style = MaterialTheme.typography.h6,
                        modifier = Modifier.padding(top = 32.dp, bottom = 64.dp),
                        textAlign = TextAlign.Center
                    )
                }
                is UiState.Error -> {
                    Text(state.message)
                }
                is UiState.Success -> {
                    MoviesGrid(
                        movies = state.data.results,
                        modifier = Modifier.weight(1f)
                    ) {
                        SearchResultFragmentDirections.actionSearchResultFragmentToDetailFragment(it.id).let {
                            findNavController().navigate(it)
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun SearchBar(modifier: Modifier = Modifier) {
        OutlinedTextField(
            value = viewModel.searchQuery,
            onValueChange = { viewModel.search(it) },
            label = { Text("Search") },
            placeholder = { Text("Top Gun: Maverick") },
            leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "Search") },
            singleLine = true,
            modifier = modifier
                .padding(vertical = 16.dp)
                .fillMaxWidth(0.9f)
        )
    }
}