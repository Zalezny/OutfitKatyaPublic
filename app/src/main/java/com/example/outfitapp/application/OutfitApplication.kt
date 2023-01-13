package com.example.outfitapp.application

import android.app.Application
import com.example.outfitapp.roomdatabase.AppDatabase

class OutfitApplication : Application() {


    private val database by lazy { AppDatabase.getDatabase(this@OutfitApplication) }

    // A variable for repository.
    val repository by lazy { database.outfitDao() }
}