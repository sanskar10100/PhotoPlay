package dev.sanskar.photoplay.ui.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sanskar.photoplay.data.Movie
import dev.sanskar.photoplay.data.MovieWatchlistInclusion
import dev.sanskar.photoplay.data.MoviesResponse
import dev.sanskar.photoplay.data.Repository
import dev.sanskar.photoplay.db.Watchlist
import dev.sanskar.photoplay.util.UiState
import javax.inject.Inject
import kotlinx.coroutines.launch
import logcat.logcat

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repo: Repository
) : ViewModel() {
    var popularMoviesResponse by mutableStateOf<UiState<MoviesResponse>>(UiState.Loading)
    var topRatedMoviesResponse by mutableStateOf<UiState<MoviesResponse>>(UiState.Loading)

    init {
        getPopularMovies()
    }

    fun getPopularMovies() {
        popularMoviesResponse = UiState.Loading
        viewModelScope.launch {
            popularMoviesResponse = repo.getPopularMovies()
        }
    }

    fun getTopRatedMovies() {
        topRatedMoviesResponse = UiState.Loading
        viewModelScope.launch {
            topRatedMoviesResponse = repo.getTopRatedMovies()
        }
    }
}