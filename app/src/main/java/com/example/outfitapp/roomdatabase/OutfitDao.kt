package com.example.outfitapp.roomdatabase

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface OutfitDao {
    @Query("SELECT * FROM outfit")
    suspend fun getAll(): List<Outfit>

    @Insert
    suspend fun insertAll(vararg outfitName: Outfit)

    @Delete
    suspend fun delete(outfitName: Outfit)

}