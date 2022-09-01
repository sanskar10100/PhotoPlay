package dev.sanskar.photoplay.ui.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sanskar.photoplay.data.Movie
import dev.sanskar.photoplay.data.Repository
import dev.sanskar.photoplay.util.UiState
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repo: Repository
) : ViewModel() {
    var popularMovies by mutableStateOf<UiState<List<Movie>>>(UiState.Loading)
    var topRatedMovies by mutableStateOf<UiState<List<Movie>>>(UiState.Loading)

    init {
        getPopularMovies()
        getTopRatedMovies()
    }

    fun getPopularMovies() {
        viewModelScope.launch {
             popularMovies = repo.getPopularMovies()
        }
    }

    fun getTopRatedMovies() {
        viewModelScope.launch {
            topRatedMovies = repo.getTopRatedMovies()
        }
    }
}