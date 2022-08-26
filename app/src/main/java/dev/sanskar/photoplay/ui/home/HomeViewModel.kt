package dev.sanskar.photoplay.ui.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sanskar.photoplay.data.MoviesResponse
import dev.sanskar.photoplay.data.Repository
import dev.sanskar.photoplay.network.MoviesBackendService
import dev.sanskar.photoplay.util.UiState
import dev.sanskar.photoplay.util.networkResult
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repo: Repository
) : ViewModel() {
    val moviesResponseMovies = MutableStateFlow<UiState<MoviesResponse>>(UiState.Loading)

    val showCreateWatchlistDialog by mutableStateOf(false)
    val showAddMovieToWatchlistDialog by mutableStateOf(false)

    init {
        getPopularMovies()
    }

    fun getPopularMovies() {
        moviesResponseMovies.value = UiState.Loading
        viewModelScope.launch {
            moviesResponseMovies.value = repo.getPopularMovies()
        }
    }

    fun getTopRatedMovies() {
        moviesResponseMovies.value = UiState.Loading
        viewModelScope.launch {
            moviesResponseMovies.value = repo.getTopRatedMovies()
        }
    }
}