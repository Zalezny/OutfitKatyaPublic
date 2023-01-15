package com.example.outfitapp.roomdatabase.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class KatyaTime(
    @PrimaryKey val kid: Int,
    @ColumnInfo(name = "outfit_creator_id") val outfitCreatorId: Int,
    @ColumnInfo(name = "hour") val hour: Int,
    @ColumnInfo(name = "minute") val minute: Int,
    @ColumnInfo(name = "second") val second: Int,
    @ColumnInfo(name = "date") val date: String,
)