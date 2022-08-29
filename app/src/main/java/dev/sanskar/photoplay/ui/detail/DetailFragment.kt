package dev.sanskar.photoplay.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import dev.sanskar.photoplay.ui.composables.ErrorDialog
import dev.sanskar.photoplay.ui.composables.ProgressBar
import dev.sanskar.photoplay.ui.theme.PhotoPlayTheme
import dev.sanskar.photoplay.util.UiState

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
                    Text(text = "Received movie name: ${state.data.title}")
                }
            }
        }
    }
}