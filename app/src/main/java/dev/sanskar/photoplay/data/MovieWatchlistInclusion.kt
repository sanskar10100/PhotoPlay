package dev.sanskar.photoplay.data

import dev.sanskar.photoplay.db.Watchlist

data class MovieWatchlistInclusion(
    val movie: Movie = Movie(),
    val watchlistInclusionStatus: List<Pair<Watchlist, Boolean>> = emptyList()
)
