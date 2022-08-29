package dev.sanskar.photoplay.ui.detail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sanskar.photoplay.data.MovieDetails
import dev.sanskar.photoplay.data.Repository
import dev.sanskar.photoplay.util.UiState
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@HiltViewModel
class DetailViewModel @Inject constructor(
    val repo: Repository
) : ViewModel() {

    var movieDetails by mutableStateOf<UiState<MovieDetails>>(UiState.Loading)

    fun getMovieDetails(movieId: Int) {
        viewModelScope.launch {
            delay(1000)
            movieDetails = UiState.Error("Error UI test")
            delay(5000)
            movieDetails = repo.getMovieDetails(movieId)
        }
    }
}