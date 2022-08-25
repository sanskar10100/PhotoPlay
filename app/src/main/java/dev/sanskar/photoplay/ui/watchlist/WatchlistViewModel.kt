package dev.sanskar.photoplay.ui.watchlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sanskar.photoplay.db.Watchlist
import dev.sanskar.photoplay.db.WatchlistDao
import dev.sanskar.photoplay.util.UiState
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

    fun getWatchlists() {
        viewModelScope.launch {
            db.getAllWatchlists().collect {
                watchlists.value = if (it.isEmpty()) UiState.Empty else UiState.Success(it)
            }
        }
    }

    fun addSample() {
        viewModelScope.launch {
            db.addWatchlist(Watchlist(
                0,
                "Personal",
                "A collection of personally liked movies",
                "some date"
            ))
        }
    }

}