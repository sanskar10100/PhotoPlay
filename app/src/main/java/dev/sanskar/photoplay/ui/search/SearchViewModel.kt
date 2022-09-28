package dev.sanskar.photoplay.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sanskar.photoplay.data.MoviesResponse
import dev.sanskar.photoplay.data.Repository
import dev.sanskar.photoplay.network.MoviesBackendService
import dev.sanskar.photoplay.util.UiState
import dev.sanskar.photoplay.util.networkResult
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repo: Repository
) : ViewModel() {

    val searchResult = MutableStateFlow<UiState<MoviesResponse>>(UiState.Loading)

    fun search(query: String) {
        viewModelScope.launch {
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