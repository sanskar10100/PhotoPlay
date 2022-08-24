package dev.sanskar.photoplay.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sanskar.photoplay.data.MoviesResponse
import dev.sanskar.photoplay.network.MoviesBackendService
import dev.sanskar.photoplay.util.UiState
import dev.sanskar.photoplay.util.networkResult
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val api: MoviesBackendService
) : ViewModel() {
    val moviesResponseMovies = MutableStateFlow<UiState<MoviesResponse>>(UiState.Loading)

    init {
        getTopRated()
    }

    private fun getTopRated() {
        viewModelScope.launch {
            moviesResponseMovies.value = networkResult {
                api.getTopRatedMovies()
            }
        }
    }
}