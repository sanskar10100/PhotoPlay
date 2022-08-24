package dev.sanskar.photoplay.util

import java.io.IOException
import logcat.logcat
import retrofit2.Response

suspend fun <T> networkResult(call: suspend () -> Response<T>): UiState<T> {
    return try {
        val result = call()
        if (result.isSuccessful) {
            logcat("NetworkResult") { "Network call successful for $call" }
            UiState.Success(result.body()!!)
        } else {
            logcat("NetworkResult") { "Network call failed for $call, " +
                    "message: ${result.message()}, code: ${result.code()}, errorBody: ${result.errorBody()}, body: ${result.body()}" }
            UiState.Error("There was an error, code: ${result.code()}")
        }
    } catch (e: IOException) {
        e.printStackTrace()
        logcat("NetworkResult") { "Network call failed for $call, exception: $e, message: ${e.message}" }
        UiState.Error("Network call failed, exception: $e")
    }
}