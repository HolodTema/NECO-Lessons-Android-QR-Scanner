package com.terabyte.jetpackqrscanner.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [Product::class],
    version = 1
)
abstract class MainDB: RoomDatabase() {
    abstract val dao: Dao
}