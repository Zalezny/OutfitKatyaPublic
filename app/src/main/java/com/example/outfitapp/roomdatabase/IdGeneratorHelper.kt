package com.example.outfitapp.roomdatabase

import android.content.Context

class IdGeneratorHelper(context : Context) {

    private val sharedPref = context.getSharedPreferences(
        "id_generator", Context.MODE_PRIVATE)

    fun takeNewId() : Int {

        val freeValue = sharedPref.getInt("id_value", 0)

        with (sharedPref.edit()) {
            putInt("id_value", freeValue + 1)
            apply()
        }
        return freeValue
    }

    fun readCurrentValue() : Int {
        return sharedPref.getInt("id_value", 0)
    }
}