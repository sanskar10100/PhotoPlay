package dev.sanskar.photoplay.ui.detail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.input.key.Key.Companion.U
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sanskar.photoplay.data.Movie
import dev.sanskar.photoplay.data.MovieCast
import dev.sanskar.photoplay.data.MovieDetails
import dev.sanskar.photoplay.data.MovieWatchlistInclusion
import dev.sanskar.photoplay.data.MoviesResponse
import dev.sanskar.photoplay.data.Repository
import dev.sanskar.photoplay.db.Watchlist
import dev.sanskar.photoplay.util.UiState
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import logcat.logcat

@HiltViewModel
class DetailViewModel @Inject constructor(
    val repo: Repository
) : ViewModel() {

    var movieDetails by mutableStateOf<UiState<MovieDetails>>(UiState.Loading)
    var movieCast by mutableStateOf<UiState<MovieCast>>(UiState.Loading)
    var movieRecommendations by mutableStateOf<UiState<MoviesResponse>>(UiState.Loading)

    var showAddMovieToWatchlistDialog by mutableStateOf(false)
    var movieWithWatchlistInclusionStatus = MovieWatchlistInclusion()

    fun addWatchlist(title: String, description: String) {
        if (title.isNotEmpty()) viewModelScope.launch {
            showAddMovieToWatchlistDialog = false
            repo.addWatchlist(title, description)
            getMovieWithWatchlistInclusionStatus(movieWithWatchlistInclusionStatus.movie)
        }
    }

    fun getMovieWithWatchlistInclusionStatus(movie: Movie) {
        viewModelScope.launch {
            logcat { "Launching viewModelScope for getting movies with watchlist inclusions" }
            movieWithWatchlistInclusionStatus = MovieWatchlistInclusion(
                movie,
                repo.getMovieWithWatchlistInclusionStatus(movie.id)
            )
            logcat { "Received $movieWithWatchlistInclusionStatus" }
            showAddMovieToWatchlistDialog = true
        }
    }

    fun updateWatchlistInclusionsForMovie(inclusionList: List<Pair<Watchlist, Boolean>>, movie: Movie) {
        if (inclusionList.isNotEmpty()) viewModelScope.launch {
            repo.updateWatchlistInclusionsForMovie(inclusionList, movie.id, movie.title, movie.poster_path, movie.backdrop_path)
        }
        showAddMovieToWatchlistDialog = false
    }

    fun getMovieDetails(movieId: Int) {
        viewModelScope.launch {
            movieDetails = repo.getMovieDetails(movieId)
            movieCast = repo.getMovieCast(movieId)
            movieRecommendations = repo.getMovieRecommendations(movieId)
        }
    }
}