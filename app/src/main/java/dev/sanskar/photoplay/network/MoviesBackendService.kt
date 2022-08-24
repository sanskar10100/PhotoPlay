package dev.sanskar.photoplay.network

import dev.sanskar.photoplay.data.MoviesResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface MoviesBackendService {

    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("page") page: Int = 1,
    ): Response<MoviesResponse>

    @GET("search/movie")
    suspend fun getMoviesForQuery(
        @Query("query") query: String,
        @Query("page") page: Int = 1,
    ): Response<MoviesResponse>
}