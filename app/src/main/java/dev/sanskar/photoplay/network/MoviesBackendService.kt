package dev.sanskar.photoplay.network

import dev.sanskar.photoplay.data.MovieCast
import dev.sanskar.photoplay.data.MovieDetails
import dev.sanskar.photoplay.data.MoviesResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MoviesBackendService {

    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("page") page: Int = 1,
    ): Response<MoviesResponse>

    @GET("movie/top_rated")
    suspend fun getTopRatedMovies(
        @Query("page") page: Int = 1,
    ): Response<MoviesResponse>

    @GET("search/movie")
    suspend fun getMoviesForQuery(
        @Query("query") query: String,
        @Query("page") page: Int = 1,
    ): Response<MoviesResponse>

    @GET("movie/{movie_id}")
    suspend fun getMovieDetails(
        @Path("movie_id") movieId: Int
    ): Response<MovieDetails>

    @GET("movie/{movie_id}/credits")
    suspend fun getMovieCast(
        @Path("movie_id") movieId: Int
    ): Response<MovieCast>

    @GET("movie/{movie_id}/recommendations")
    suspend fun getMovieRecommendations(
        @Path("movie_id") movieId: Int,
    ): Response<MoviesResponse>
}