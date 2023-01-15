package com.example.outfitapp.roomdatabase

import androidx.room.*

@Entity
data class Outfit(
                  @PrimaryKey val oid: Int,
                  @ColumnInfo(name = "outfit_name") val outfitName: String?,
                  @ColumnInfo(name = "is_ended") val isEnded: Boolean?,
                  @ColumnInfo(name = "date") val date: String?,

                  )





