package dev.sanskar.photoplay.data

import dev.sanskar.photoplay.db.MovieEntity
import dev.sanskar.photoplay.db.Watchlist
import dev.sanskar.photoplay.db.WatchlistDao
import dev.sanskar.photoplay.network.MoviesBackendService
import dev.sanskar.photoplay.util.UiState
import dev.sanskar.photoplay.util.networkResult
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject
import kotlinx.coroutines.flow.map

class Repository @Inject constructor(
    private val api: MoviesBackendService,
    private val db: WatchlistDao
) {

    fun getWatchlists() = db.getAllWatchlists().map { if (it.isEmpty()) UiState.Empty else UiState.Success(it) }

    suspend fun addWatchlist(title: String, description: String) = db.addWatchlist(Watchlist(
        0,
        title,
        description,
        SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault()).format(System.currentTimeMillis())
    ))

    suspend fun getMoviesForWatchlist(watchlistId: Int): UiState<Pair<Watchlist, List<MovieEntity>>> {
        val watchlist = db.getWatchlist(watchlistId)
        return if (watchlist != null) {
            val movies = db.getAllMovies(watchlistId)
            if (movies.isNotEmpty()) UiState.Success(Pair(watchlist, movies)) else UiState.Empty
        } else {
            UiState.Empty
        }
    }

    suspend fun getTopRatedMovies() = networkResult { api.getTopRatedMovies() }

    suspend fun getPopularMovies() = networkResult { api.getPopularMovies() }

    suspend fun searchMovie(query: String) = networkResult { api.getMoviesForQuery(query) }

    suspend fun getMovieWithWatchlistInclusionStatus(movieId: Int): List<Pair<Watchlist, Boolean>> {
        val movieEntries = db.getMovieEntries(movieId)
        val watchlists = db.getAllWatchlistsOneShot()
        val checkList = mutableListOf<Pair<Watchlist, Boolean>>()
        watchlists.forEach { watchlist ->
            if (movieEntries.any { it.id == watchlist.id }) {
                checkList.add(Pair(watchlist, true))
            } else {
                checkList.add(Pair(watchlist, false))
            }
        }
        return checkList
    }

    suspend fun updateWatchlistInclusionsForMovie(includeIn: List<Int>, id: Int, title: String, posterPath: String?, backdropPath: String?) {
        val watchlists = db.getAllWatchlistsOneShot()
        watchlists.forEach { watchlist ->
            if (watchlist.id in includeIn) {
                db.addMovie(MovieEntity(id, title, backdropPath, posterPath, watchlist.id))
            } else {
                db.deleteMovie(id, watchlist.id)
            }
        }
    }
}