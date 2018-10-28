package com.tpb.coinz

import android.annotation.SuppressLint
import android.content.Context
import com.mapbox.android.core.location.LocationEngine
import com.mapbox.android.core.location.LocationEngineListener
import com.mapbox.android.core.location.LocationEngineProvider


object LocationListener {

    private lateinit var locationEngine: LocationEngine

    public fun init(context: Context) {
        locationEngine = LocationEngineProvider(context).obtainBestLocationEngineAvailable()
        locationEngine.activate()
    }

    public fun activate() {
        locationEngine.activate()
    }

    public fun deactivate() {
        locationEngine.deactivate()
    }

    @SuppressLint("MissingPermission")
    public fun lastLocation() = locationEngine.lastLocation

    public fun addListener(listener: LocationEngineListener) = locationEngine.addLocationEngineListener(listener)

    public fun getEngine(): LocationEngine = locationEngine

}