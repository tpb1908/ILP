package com.tpb.coinz

import android.annotation.SuppressLint
import android.content.Context
import android.os.Looper
import android.util.Log
import com.google.android.gms.location.*
import com.mapbox.android.core.location.LocationEngine
import com.mapbox.android.core.location.LocationEngineListener
import com.mapbox.android.core.location.LocationEnginePriority
import com.mapbox.android.core.location.LocationEngineProvider
import java.util.concurrent.TimeUnit


object LocationListener {

    private lateinit var locationEngine: LocationEngine

    public fun init(context: Context) {
        locationEngine = LocationEngineProvider(context).obtainBestLocationEngineAvailable()
        locationEngine.priority = LocationEnginePriority.HIGH_ACCURACY
        activate()
        try {
            val req = LocationRequest()
            req.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            req.interval = TimeUnit.SECONDS.toMillis(1)
            val fusedLocationProviderClient = FusedLocationProviderClient(context)
            fusedLocationProviderClient.requestLocationUpdates(req, object: LocationCallback() {

                override fun onLocationResult(p0: LocationResult?) {
                    super.onLocationResult(p0)
                }

                override fun onLocationAvailability(p0: LocationAvailability?) {
                    super.onLocationAvailability(p0)
                }
            }, Looper.myLooper())
            fusedLocationProviderClient.lastLocation.addOnCompleteListener {
                Log.i(LocationListener::class.java.name, "Last location ${it.result}")
            }
        } catch (se: SecurityException) {
            Log.e(LocationListener::class.java.name, "Missing permission", se)
        }
    }

    public fun activate() {
        locationEngine.activate()
    }

    public fun deactivate() {
        locationEngine.deactivate()
    }

    @SuppressLint("MissingPermission")
    public fun lastLocation() = locationEngine.lastLocation

    public fun addListener(listener: LocationEngineListener) =
        locationEngine.addLocationEngineListener(listener)

    public fun getEngine(): LocationEngine = locationEngine


}