package com.example.outfitapp.stopwatch

sealed class StopwatchEvent{
    object START : StopwatchEvent()
    object END : StopwatchEvent()
}
