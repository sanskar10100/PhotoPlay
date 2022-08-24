package dev.sanskar.photoplay.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sanskar.photoplay.data.MoviesResponse
import dev.sanskar.photoplay.network.MoviesBackendService
import dev.sanskar.photoplay.util.UiState
import dev.sanskar.photoplay.util.networkResult
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val api: MoviesBackendService
) : ViewModel() {

    val searchResult = MutableStateFlow<UiState<MoviesResponse>>(UiState.Loading)

    fun search(query: String) {
        viewModelScope.launch {
            searchResult.value = networkResult {
                api.getMoviesForQuery(query)
            }
        }
    }
}