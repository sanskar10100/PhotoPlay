package dev.sanskar.photoplay.ui.search

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sanskar.photoplay.data.MoviesResponse
import dev.sanskar.photoplay.data.Repository
import dev.sanskar.photoplay.network.MoviesBackendService
import dev.sanskar.photoplay.util.UiState
import dev.sanskar.photoplay.util.networkResult
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repo: Repository
) : ViewModel() {

    val searchResult = MutableStateFlow<UiState<MoviesResponse>>(UiState.Loading)

    var searchQuery by mutableStateOf("")

    var searchJob: Job? = null

    fun search(query: String) {
        searchQuery = query
        searchJob?.cancel()
        if (query.isNotEmpty()) searchJob = viewModelScope.launch {
            searchResult.value = repo.searchMovie(query).let {
                if (it is UiState.Success && it.data.results.isEmpty()) {
                    UiState.Empty
                } else {
                    it
                }
            }
        }
    }
}