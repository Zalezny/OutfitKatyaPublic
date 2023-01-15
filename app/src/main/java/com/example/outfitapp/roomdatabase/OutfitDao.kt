package com.example.outfitapp.roomdatabase

import androidx.room.*
import com.example.outfitapp.roomdatabase.entities.OutfitWithKatyaTime

@Dao
interface OutfitDao {
    @Query("SELECT * FROM outfit")
    suspend fun getOutfits(): List<Outfit>

    @Insert
    suspend fun insertOutfit(vararg outfitName: Outfit)

    @Query("UPDATE outfit SET is_ended= :isEnded WHERE oid = :outfitId")
    suspend fun setIsEndedById(outfitId: Int, isEnded: Boolean): Int

    @Query("DELETE FROM outfit WHERE oid = :outfitId")
    suspend fun deleteByOutfitId(outfitId: Int)

    @Delete
    suspend fun delete(outfitName: Outfit)

    @Transaction
    @Query("SELECT * FROM outfit WHERE oid = :outfitId")
    suspend fun getOutfitWithKatyaTime(outfitId: Int) : List<OutfitWithKatyaTime>


}



