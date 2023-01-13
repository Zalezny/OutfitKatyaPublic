package com.example.outfitapp.models

data class HoursDataModel(var _id : String,
                          var title : String,
                          var date: String,
                          var momHours : ArrayList<Time>,
                          var kateHours : ArrayList<Time>
                          )

class Time(var hour : Int,
           var minute : Int,
           var second : Int,
           var _id : String,
           var date : String
           )
