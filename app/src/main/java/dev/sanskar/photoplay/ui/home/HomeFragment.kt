package dev.sanskar.photoplay.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.with
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Theaters
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import dagger.hilt.android.AndroidEntryPoint
import dev.sanskar.photoplay.R
import dev.sanskar.photoplay.ui.composables.AddMovieToWatchLists
import dev.sanskar.photoplay.ui.composables.MoviesGrid
import dev.sanskar.photoplay.ui.composables.ProgressBar
import dev.sanskar.photoplay.ui.composables.ShortErrorSnackbar
import dev.sanskar.photoplay.ui.theme.PhotoPlayTheme
import dev.sanskar.photoplay.util.UiState
import dev.sanskar.photoplay.util.clickWithRipple
import kotlinx.coroutines.launch
import logcat.logcat

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private val viewModel by viewModels<HomeViewModel>()

    @OptIn(ExperimentalPagerApi::class)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                PhotoPlayTheme {
                    val scaffoldState = rememberScaffoldState()
                    val pagerState = rememberPagerState()
                    Scaffold(
                        topBar = { HomeAppBar(pagerState) },
                        scaffoldState = scaffoldState,
                    ) {
                        HomeContent(scaffoldState, pagerState, modifier = Modifier.padding(it))
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalPagerApi::class)
    @Composable
    fun HomeContent(scaffoldState: ScaffoldState, pagerState: PagerState, modifier: Modifier = Modifier) {
        logcat { "Home Content Recomposed" }
        val scope = rememberCoroutineScope()
        HorizontalPager(
            count = 2,
            state = pagerState,
        ) { page ->
            when (page) {
                0 -> {
                    LaunchedEffect(Unit) { viewModel.getPopularMovies() }
                    when (val state = viewModel.popularMoviesResponse) {
                        is UiState.Loading -> {
                            ProgressBar(true)
                        }
                        is UiState.Empty -> {}
                        is UiState.Error -> {
                            scaffoldState.ShortErrorSnackbar(message = state.message)
                        }
                        is UiState.Success -> {
                            MoviesGrid(movies = state.data.results) {
                                HomeFragmentDirections.actionHomeFragmentToDetailFragment(it.id).let {
                                    findNavController().navigate(it)
                                }
                            }
                        }
                    }
                }
                1 -> {
                    LaunchedEffect(Unit) { viewModel.getTopRatedMovies() }
                    BackHandler(pagerState.currentPage == 1) {
                        scope.launch {
                            pagerState.animateScrollToPage(0)
                        }
                    }
                    when (val state = viewModel.topRatedMoviesResponse) {
                        is UiState.Loading -> {
                            ProgressBar(true)
                        }
                        is UiState.Empty -> {}
                        is UiState.Error -> {
                            scaffoldState.ShortErrorSnackbar(message = state.message)
                        }
                        is UiState.Success -> {
                            MoviesGrid(movies = state.data.results) {
                                HomeFragmentDirections.actionHomeFragmentToDetailFragment(it.id).let {
                                    findNavController().navigate(it)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalAnimationApi::class, ExperimentalPagerApi::class)
    @Composable
    fun HomeAppBar(pagerState: PagerState) {
        var searchMode by remember { mutableStateOf(false) }
        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.movies))
        val progress by animateLottieCompositionAsState(composition,
            iterations = LottieConstants.IterateForever)
        BackHandler(searchMode) {
            searchMode = false
        }

        AnimatedContent(
            targetState = searchMode,
            transitionSpec = {
                slideIntoContainer(AnimatedContentScope.SlideDirection.Up,
                    animationSpec = tween(500)) with slideOutOfContainer(
                    AnimatedContentScope.SlideDirection.Up, animationSpec = tween(500))
            },
            modifier = Modifier.fillMaxWidth(),
        ) { state ->
            if (state) {
                SearchField {
                    searchMode = false
                    HomeFragmentDirections.actionHomeFragmentToSearchResultFragment(it).let {
                        findNavController().navigate(it)
                    }
                }
            } else {
                Column {
                    Box(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Theaters,
                            contentDescription = "Watchlist",
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .padding(start = 16.dp)
                                .scale(1.5f)
                                .clickWithRipple {
                                    findNavController().navigate(R.id.action_homeFragment_to_watchlistFragment)
                                }
                        )
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
                    val scope = rememberCoroutineScope()
                    TabRow(
                        selectedTabIndex = pagerState.currentPage,
                        indicator = { tabPositions ->
                            TabRowDefaults.Indicator(
                                Modifier.pagerTabIndicatorOffset(pagerState, tabPositions)
                            )
                        }
                    ) {
                        Tab(
                            selected = pagerState.currentPage == 0,
                            onClick = {
                                scope.launch {
                                    pagerState.animateScrollToPage(0)
                                }
                            },
                            text = {
                                Text("Popular")
                            }
                        )
                        Tab(
                            selected = pagerState.currentPage == 1,
                            onClick = {
                                scope.launch {
                                    pagerState.animateScrollToPage(1)
                                }
                            },
                            text = {
                                Text("Top Rated")
                            }
                        )
                    }
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
        val focusRequester = remember { FocusRequester() }
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
                .focusRequester(focusRequester)
        )

        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }
    }
}