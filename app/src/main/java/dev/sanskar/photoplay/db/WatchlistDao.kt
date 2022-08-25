package dev.sanskar.photoplay.db

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

interface WatchlistDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun createWatchlist(watchlist: Watchlist)

    @Query("SELECT * FROM watchlist")
    suspend fun getAllWatchlists(): List<Watchlist>

    @Query("SELECT * FROM watchlist WHERE id = :watchlistId")
    suspend fun getWatchlist(watchlistId: Int)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addMovie(movieEntity: MovieEntity)

    @Query("SELECT * FROM table_movies")
    suspend fun getAllMovies(): List<MovieEntity>

    @Query("SELECT * FROM table_movies WHERE watchlistId = :watchlistId")
    suspend fun getAllMovies(watchlistId: Int): List<MovieEntity>

    @Transaction
    suspend fun deleteWatchlist(watchlistId: Int) {
        deleteMovies(watchlistId)
        deleteWatchlistEntity(watchlistId)
    }

    @Query("DELETE FROM watchlist WHERE id = :watchlistId")
    suspend fun deleteWatchlistEntity(watchlistId: Int)

    @Query("DELETE FROM table_movies WHERE watchlistId = :watchlistId")
    suspend fun deleteMovies(watchlistId: Int)

    @Query("DELETE FROM table_movies WHERE id = :id")
    suspend fun deleteMovie(movieId: Int)
}