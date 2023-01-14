package com.example.outfitapp.roomdatabase

import androidx.room.*

@Entity
data class Outfit(
                  @PrimaryKey val oid: Int,
                  @ColumnInfo(name = "outfit_name") val outfitName: String?,
                  @ColumnInfo(name = "is_ended") val isEnded: Boolean?,
                  @ColumnInfo(name = "date") val date: String?,

                  )

@Entity
data class KatyaTime(
    @PrimaryKey val kid: Int,
    @ColumnInfo(name = "outfit_creator_id") val outfitCreatorId: Int,
    @ColumnInfo(name = "hour") val hour: Int,
    @ColumnInfo(name = "minute") val minute: Int,
    @ColumnInfo(name = "second") val second: Int,
    @ColumnInfo(name = "date") val date: String,
)

data class OutfitWithKatyaTime(
    @Embedded val outfit: Outfit,
    @Relation(
        parentColumn = "oid",
        entityColumn = "outfit_creator_id"
    )
    val hours: List<KatyaTime>

)

