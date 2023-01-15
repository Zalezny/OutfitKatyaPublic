package com.example.outfitapp.roomdatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.outfitapp.roomdatabase.entities.KatyaTime

@Database(entities = [Outfit::class, KatyaTime::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun outfitDao(): OutfitDao
    abstract fun katyaTimeDao() : KatyaTimeDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "outfit-database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }

}

