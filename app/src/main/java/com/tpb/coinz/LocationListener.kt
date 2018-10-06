package com.tpb.coinz

import android.content.Context
import android.location.Location
import android.os.Looper
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.google.android.gms.location.*
import java.util.concurrent.TimeUnit

class LocationListener(private val context: Context,
                       private val callback: (Location) -> Unit,
                       private val fusedLocationProviderClient: FusedLocationProviderClient = FusedLocationProviderClient(context)
): LocationCallback(), LifecycleObserver {

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun start() {
        Log.i(LocationListener::class.java.name, "Lifecyle onStart called")
        try {
            val req = LocationRequest()
            req.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            req.interval = TimeUnit.SECONDS.toMillis(1)

            fusedLocationProviderClient.requestLocationUpdates(req, this, Looper.myLooper())
            fusedLocationProviderClient.lastLocation.addOnCompleteListener {
                Log.i(LocationListener::class.java.name, "Last location ${it.result}")
            }
        } catch (se: SecurityException) {
            Log.e(LocationListener::class.java.name, "Missing permission", se)
        }
    }

    fun requestLastLocation(callback: (Location) -> Unit) {
        fusedLocationProviderClient.lastLocation.addOnCompleteListener {
            val loc = it.result
            if (loc != null) callback(loc)
        }
    }

    override fun onLocationResult(result: LocationResult?) {
        super.onLocationResult(result)
        Log.i(LocationListener::class.java.name, "Location result " + result?.lastLocation.toString())
        if (result != null) callback(result.lastLocation)
    }

    override fun onLocationAvailability(p0: LocationAvailability?) {
        super.onLocationAvailability(p0)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun stop() {
        Log.i(LocationListener::class.java.name, "Location listener pausing")
        // disconnect if connected
        fusedLocationProviderClient.removeLocationUpdates(this)
    }
}