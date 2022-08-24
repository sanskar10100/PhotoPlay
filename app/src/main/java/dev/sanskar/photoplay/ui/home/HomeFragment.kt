package dev.sanskar.photoplay.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImagePainter.State.Empty.painter
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import dagger.hilt.android.AndroidEntryPoint
import dev.sanskar.photoplay.R
import dev.sanskar.photoplay.ui.composables.MoviesGrid
import dev.sanskar.photoplay.ui.composables.ProgressBar
import dev.sanskar.photoplay.ui.theme.PhotoPlayTheme
import dev.sanskar.photoplay.util.UiState
import dev.sanskar.photoplay.util.clickWithRipple

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private val viewModel by viewModels<HomeViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                PhotoPlayTheme {
                    Scaffold(
                        topBar = { HomeAppBar() }
                    ) {
                        HomeContent(Modifier.padding(it))
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalLifecycleComposeApi::class)
    @Composable
    fun HomeContent(modifier: Modifier = Modifier) {
        val state by viewModel.topRatedMovies.collectAsStateWithLifecycle()
        val loading = derivedStateOf {
            state is UiState.Loading
        }
        when (val state = state) {
            is UiState.Loading -> {
            }
            is UiState.Error -> {
                Text("Error, message: ${state.message}")
            }
            is UiState.Success -> {
                MoviesGrid(movies = state.data.results, modifier)
            }
        }
        ProgressBar(loading.value)
    }

    @OptIn(ExperimentalAnimationApi::class)
    @Composable
    fun HomeAppBar() {
        var searchMode by remember { mutableStateOf(false) }
        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.movies))
        val progress by animateLottieCompositionAsState(composition, iterations = LottieConstants.IterateForever)
        BackHandler(searchMode) {
            searchMode = false
        }

        AnimatedContent(targetState = searchMode, modifier = Modifier.fillMaxWidth()) { state ->
            if (state) {
                SearchField {
                    searchMode = false
                }
            } else {
                Column {
                    Box(
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        LottieAnimation(
                            composition = composition,
                            progress = { progress },
                            modifier = Modifier
                                .height(72.dp)
                                .align(Alignment.Center)
                        )
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .padding(end = 16.dp)
                                .scale(1.5f)
                                .clickWithRipple {
                                    searchMode = true
                                }
                        )
                    }
                    Divider()
                }
            }
        }
    }

    @Composable
    fun SearchField(
        modifier: Modifier = Modifier,
        onSearched: (String) -> Unit,
    ) {
        var text by remember { mutableStateOf("") }
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("Query") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search
            ),
            leadingIcon = {
                Icon(Icons.Default.Search,
                    contentDescription = null)
            },
            keyboardActions = KeyboardActions(
                onSearch = { onSearched(text) }
            ),
            modifier = modifier
                .fillMaxWidth()
                .padding(8.dp)
        )
    }
}