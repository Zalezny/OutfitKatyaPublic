package com.example.outfitapp.roomdatabase

import android.content.Context
import android.util.Log

class IdGeneratorHelper(context : Context) {

    private val sharedPref = context.getSharedPreferences(
        "id_generator", Context.MODE_PRIVATE)
    private val OUTFIT_VALUE_KEY = "id_value_outfit"
    private val KATYA_TIME_VALUE_KEY = "id_value_katya_time"

    fun takeNewOutfitId() : Int {

        val freeValue = sharedPref.getInt(OUTFIT_VALUE_KEY, 0)

        with (sharedPref.edit()) {
            putInt(OUTFIT_VALUE_KEY, freeValue + 1)
            apply()
        }
        return freeValue
    }

    fun readCurrentOutfitValue() : Int {
        return sharedPref.getInt(OUTFIT_VALUE_KEY, 0)
    }

    fun takeNewKatyaTimeId() : Int {
        val freeValue = sharedPref.getInt(KATYA_TIME_VALUE_KEY, 0)

        with (sharedPref.edit()) {
            putInt(KATYA_TIME_VALUE_KEY, freeValue + 1)
            apply()
        }
        return freeValue
    }
}