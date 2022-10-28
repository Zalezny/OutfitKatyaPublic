package com.example.outfitapp

/**
 * That class is responsible for calculating total time for Katya and Mom Fragment
 **/

class TotalTimeHelper {

    //total hours
    private var totalH = 0
    //total minutes
    private var totalM = 0
    //total seconds
    private var totalS = 0

    //remove time from total time
    fun removeTotalTime(h: Int, m: Int, s: Int) {
        totalS -= s
        if(totalS < 0) {
            totalM -= 1
            totalS += 60
        }
        totalM -= m
        if(totalM < 0){
            totalH -= 1
            totalM += 60
        }
        totalH -= h

    }


    //adding to total time a amounts hours, minutes and seconds
    fun addTotalTime(h: Int, m: Int, s: Int) {

        totalS += s
        if(totalS > 59) {
            totalM += 1
            totalS -= 60
        }
        totalM += m
        if(totalM > 59)
        {
            totalH += 1
            totalM -= 60
        }
        totalH += h
    }

    //return time in scheme 00:00:00 or 0:00:00
    fun getTime() : String {
        return "$totalH"+ ":" + (if(totalM<10) "0$totalM" else "$totalM") +
                ":" + (if (totalS<10) "0$totalS" else "$totalS")
    }

}