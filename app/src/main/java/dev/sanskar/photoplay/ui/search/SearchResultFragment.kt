package dev.sanskar.photoplay.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
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
        when (val state = state) {
            is UiState.Loading -> {
                ProgressBar(true)
            }
            is UiState.Empty -> {}
            is UiState.Error -> {
                Text(state.message)
            }
            is UiState.Success -> {
                MoviesGrid(
                    movies = state.data.results
                )
            }
        }
    }
}