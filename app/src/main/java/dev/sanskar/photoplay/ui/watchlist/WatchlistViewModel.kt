package dev.sanskar.photoplay.ui.watchlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sanskar.photoplay.data.Repository
import dev.sanskar.photoplay.db.MovieEntity
import dev.sanskar.photoplay.db.Watchlist
import dev.sanskar.photoplay.util.UiState
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class WatchlistViewModel @Inject constructor(
    private val repo: Repository
) : ViewModel() {

    init {
        getWatchlists()
    }

    val watchlists = MutableStateFlow<UiState<List<Watchlist>>>(UiState.Loading)
    val watchlistDetails = MutableStateFlow<UiState<Pair<Watchlist, List<MovieEntity>>>>(UiState.Loading)

    private fun getWatchlists() {
        viewModelScope.launch {
            repo.getWatchlists().collect { watchlists.value = it }
        }
    }

    fun addWatchlist(title: String, description: String) {
        if (title.isNotEmpty()) viewModelScope.launch {
            repo.addWatchlist(title, description)
        }
    }

    fun getMoviesForWatchlist(watchlistId: Int) {
        viewModelScope.launch {
           watchlistDetails.value = repo.getMoviesForWatchlist(watchlistId)
        }
    }

    fun removeMovieFromWatchlist(movieId: Int, watchlistId: Int) {
        viewModelScope.launch {
            repo.removeMovieFromWatchlist(movieId, watchlistId)
            getMoviesForWatchlist(watchlistId)
        }
    }

}