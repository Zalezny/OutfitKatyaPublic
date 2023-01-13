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

    @Query("UPDATE outfit SET is_ended= :isEnded WHERE oid = :outfitId")
    suspend fun setIsEndedById(outfitId: Int, isEnded: Boolean): Int

    @Query("DELETE FROM outfit WHERE oid = :outfitId")
    suspend fun deleteByOutfitId(outfitId: Int)

    @Delete
    suspend fun delete(outfitName: Outfit)

}