package com.tpb.coinz.map

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MapViewModel : ViewModel() {
    val longitude = MutableLiveData<Double>()
    val latitude = MutableLiveData<Double>()

    fun setLatitude(lat: Double) { latitude.postValue(lat) }

    fun setLongitude(long: Double) { longitude.postValue(long) }

}