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

    private val topRatedMovies = mutableListOf<Movie>()
    private val popularMovies = mutableListOf<Movie>()
    var popularMoviesPage = 1
    var topRatedMoviesPage = 1

    fun getWatchlists() =
        db.getAllWatchlists().map { if (it.isEmpty()) UiState.Empty else UiState.Success(it) }

    suspend fun addWatchlist(title: String, description: String) = db.addWatchlist(Watchlist(
        0,
        title,
        description,
        SimpleDateFormat("dd/MM/yyyy hh:mm a",
            Locale.getDefault()).format(System.currentTimeMillis())
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

    suspend fun getTopRatedMovies(): UiState<List<Movie>> {
        val response = networkResult { api.getTopRatedMovies(topRatedMoviesPage) }
        return if (response is UiState.Success) {
            topRatedMovies.addAll(response.data.results)
            topRatedMoviesPage++
            UiState.Success(topRatedMovies.toList())
        } else if (response is UiState.Error) {
            if (topRatedMovies.size == 0) {
                UiState.Error(response.message)
            } else {
                UiState.Success(topRatedMovies.toList())
            }
        } else {
            UiState.Error("Something went wrong")
        }
    }

    suspend fun getPopularMovies(): UiState<List<Movie>> {
        val response = networkResult { api.getPopularMovies(popularMoviesPage) }
        return if (response is UiState.Success) {
            popularMovies.addAll(response.data.results)
            popularMoviesPage++
            UiState.Success(popularMovies.toList())
        } else if (response is UiState.Error) {
            if (popularMovies.size == 0) {
                UiState.Error(response.message)
            } else {
                UiState.Success(popularMovies.toList())
            }
        } else {
            UiState.Error("Something went wrong")
        }
    }

    suspend fun searchMovie(query: String) = networkResult { api.getMoviesForQuery(query) }

    suspend fun getMovieDetails(movieId: Int) = networkResult { api.getMovieDetails(movieId) }

    suspend fun getMovieCast(movieId: Int) = networkResult { api.getMovieCast(movieId) }

    suspend fun getMovieRecommendations(movieId: Int) = networkResult { api.getMovieRecommendations(movieId) }

    suspend fun getMovieWithWatchlistInclusionStatus(movieId: Int): List<Pair<Watchlist, Boolean>> {
        val movieEntries = db.getMovieEntries(movieId)
        val watchlists = db.getAllWatchlistsOneShot()
        val checkList = mutableListOf<Pair<Watchlist, Boolean>>()
        watchlists.forEach { watchlist ->
            if (movieEntries.any { it.watchlistId == watchlist.id }) {
                checkList.add(Pair(watchlist, true))
            } else {
                checkList.add(Pair(watchlist, false))
            }
        }
        return checkList
    }

    suspend fun updateWatchlistInclusionsForMovie(
        includeIn: List<Pair<Watchlist, Boolean>>,
        id: Int,
        title: String,
        posterPath: String?,
        backdropPath: String?
    ) {
        includeIn.forEach { include ->
            if (!include.second) {
                db.deleteMovie(id, include.first.id)
            } else {
                db.addMovie(MovieEntity(id, title, posterPath, backdropPath, include.first.id))
            }
        }
    }

    suspend fun removeMovieFromWatchlist(movieId: Int, watchlistId: Int) =
        db.deleteMovie(movieId, watchlistId)

    suspend fun deleteWatchlist(id: Int) = db.deleteWatchlist(id)

    suspend fun updateWatchlist(watchlist: Watchlist, title: String, description: String) = db.updateWatchlist(watchlist.copy(name = title, description = description))

}