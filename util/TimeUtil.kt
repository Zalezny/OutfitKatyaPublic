package com.example.outfitapp.util

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

object TimeUtil {
    fun ddMMyyyyHHmmss(date: String): String {
        //formatters (need in format dates)
        val formatterFromJson =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
        val formatterTwo = DateTimeFormatter.ofPattern("dd-MM-yyyy, HH:mm:ss", Locale.US)


        //operation on dates
        val sdfDate = LocalDateTime.parse(date, formatterFromJson)

        return sdfDate.format(formatterTwo)
    }

    //get hour from time 0:00:00 or 00:00:00
    fun getHour(time: String) : Int{
        return if(time[1] == ':')
            time[0].digitToInt()
        else
            "${time[0]}${time[1]}".toInt()
    }
    //get minute from time 0:00:00 or 00:00:00
    fun getMinute(time : String) : Int {
        return if (time[1] == ':')
            "${time[2]}${time[3]}".toInt()
        else
            "${time[3]}${time[4]}".toInt()
    }
    //get second from time 0:00:00 or 00:00:00
    fun getSecond(time: String) : Int {
        return if (time[1] == ':')
            "${time[5]}${time[6]}".toInt()
        else
            "${time[6]}${time[7]}".toInt()
    }
}