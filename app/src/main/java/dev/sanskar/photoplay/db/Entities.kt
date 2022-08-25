package dev.sanskar.photoplay.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Watchlist(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val description: String,
    val createdOn: String,
)

@Entity(tableName = "table_movies")
data class MovieEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val backdropPath: String?,
    val posterPath: String?,
    val watchlistId: Int,
)
