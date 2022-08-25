package dev.sanskar.photoplay.ui.watchlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sanskar.photoplay.db.MovieEntity
import dev.sanskar.photoplay.db.Watchlist
import dev.sanskar.photoplay.db.WatchlistDao
import dev.sanskar.photoplay.util.UiState
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class WatchlistViewModel @Inject constructor(
    private val db: WatchlistDao
) : ViewModel() {

    init {
        getWatchlists()
    }

    val watchlists = MutableStateFlow<UiState<List<Watchlist>>>(UiState.Loading)
    val watchlistDetails = MutableStateFlow<UiState<Pair<Watchlist, List<MovieEntity>>>>(UiState.Loading)

    private fun getWatchlists() {
        viewModelScope.launch {
            db.getAllWatchlists().collect {
                watchlists.value = if (it.isEmpty()) UiState.Empty else UiState.Success(it)
            }
        }
    }

    fun addWatchlist(title: String, description: String) {
        viewModelScope.launch {
            val today = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault()).format(System.currentTimeMillis())
            db.addWatchlist(Watchlist(0, title, description, today))
        }
    }

    fun getMoviesForWatchlist(watchlistId: Int) {
        viewModelScope.launch {
            val watchlist = db.getWatchlist(watchlistId)
            if (watchlist != null) {
                val movies = db.getAllMovies(watchlistId)
                watchlistDetails.value = if (movies.isNotEmpty()) UiState.Success(Pair(watchlist, movies)) else UiState.Empty
            }
        }
    }

}