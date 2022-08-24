package dev.sanskar.photoplay.util

fun String.getDownloadUrl(): String {
    return "https://image.tmdb.org/t/p/w500/$this"
}