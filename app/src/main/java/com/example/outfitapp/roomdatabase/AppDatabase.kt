package com.example.outfitapp.roomdatabase

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Outfit::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun outfitDao(): OutfitDao

}

