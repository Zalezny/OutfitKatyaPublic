package com.example.outfitapp.roomdatabase

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.outfitapp.roomdatabase.entities.KatyaTime

@Dao
interface KatyaTimeDao {
    @Insert
    suspend fun insertKatyaTime(vararg katyaTime: KatyaTime)

    @Query("DELETE FROM katyatime WHERE kid = :katyaTimeId")
    suspend fun deleteByKatyaTimeId(katyaTimeId: Int)
}