package dev.sanskar.photoplay.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Watchlist::class, MovieEntity::class], version = 2)
abstract class PhotoPlayDB : RoomDatabase() {
    abstract fun watchlistDao() : WatchlistDao
}