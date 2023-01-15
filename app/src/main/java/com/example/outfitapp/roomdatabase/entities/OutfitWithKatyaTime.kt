package com.example.outfitapp.roomdatabase.entities

import androidx.room.Embedded
import androidx.room.Relation
import com.example.outfitapp.roomdatabase.Outfit

data class OutfitWithKatyaTime(
    @Embedded val outfit: Outfit,
    @Relation(
        parentColumn = "oid",
        entityColumn = "outfit_creator_id"
    )
    val hours: List<KatyaTime>

)