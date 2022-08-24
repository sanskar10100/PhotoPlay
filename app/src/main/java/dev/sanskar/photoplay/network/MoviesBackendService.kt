package dev.sanskar.photoplay.network

import dev.sanskar.photoplay.data.TopRated
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface MoviesBackendService {

    @GET("movie/top_rated")
    suspend fun getTopRatedMovies(
        @Query("page") page: Int = 1,
    ): Response<TopRated>
}