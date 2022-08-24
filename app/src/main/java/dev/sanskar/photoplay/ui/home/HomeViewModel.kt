package dev.sanskar.photoplay.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sanskar.photoplay.data.TopRated
import dev.sanskar.photoplay.network.MoviesBackendService
import dev.sanskar.photoplay.util.UiState
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val api: MoviesBackendService
) : ViewModel() {
    val topRatedMovies = MutableStateFlow<UiState<TopRated>>(UiState.Loading)

    init {
        getTopRated()
    }

    private fun getTopRated() {
        viewModelScope.launch {
            val apiResult = api.getTopRatedMovies()
            if (apiResult.isSuccessful) {
                topRatedMovies.value = UiState.Success(apiResult.body()!!)
            } else {
                topRatedMovies.value = UiState.Error("Error: ${apiResult.code()}")
            }
        }
    }
}