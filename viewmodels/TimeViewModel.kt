package com.example.outfitapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TimeViewModel : ViewModel() {
    private val katyaMutableLiveData = MutableLiveData<Map<String,String>>()
    private val momMutableLiveData = MutableLiveData<Map<String,String>>()

    fun setKatya(map: Map<String, String>) {
        katyaMutableLiveData.value = map
    }

    fun getKatya() : LiveData<Map<String, String>> {
        return katyaMutableLiveData
    }

    fun setMom(map: Map<String, String>) {
        momMutableLiveData.value = map
    }

    fun getMom() : LiveData<Map<String, String>> {
        return momMutableLiveData
    }
}