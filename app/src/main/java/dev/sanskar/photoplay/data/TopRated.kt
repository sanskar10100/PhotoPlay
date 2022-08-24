package dev.sanskar.photoplay.data

data class TopRated(
    val page: Int,
    val results: List<Movie>,
    val total_pages: Int,
    val total_results: Int
)