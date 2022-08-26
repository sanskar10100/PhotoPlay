package dev.sanskar.photoplay.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Watchlist(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val name: String,
    val description: String,
    val createdOn: String,
)

@Entity(tableName = "table_movies", primaryKeys = ["id", "watchlistId"])
data class MovieEntity(
    val id: Int,
    val name: String,
    val backdropPath: String?,
    val posterPath: String?,
    val watchlistId: Int,
)