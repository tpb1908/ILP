package com.tpb.coinz.data.location

import android.content.Context
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.*
import timber.log.Timber
import java.util.concurrent.TimeUnit

/**
 * Implementation of [LocationProvider] using Play Services [FusedLocationProviderClient]
 */
class GMSLocationProvider(context: Context) : LocationProvider, LocationCallback() {

    private val fusedLocationProviderClient = FusedLocationProviderClient(context)
    private val listeners: MutableSet<LocationListener> = HashSet()
    private var lastLocation: Location? = null

    override fun addListener(listener: LocationListener) {
        listeners.add(listener)
        lastLocation?.let { listener.locationUpdate(it) }
    }

    override fun removeListener(listener: LocationListener) {
        listeners.remove(listener)
    }

    override fun onLocationResult(lr: LocationResult?) {
        super.onLocationResult(lr)
        lr?.apply {
            this@GMSLocationProvider.lastLocation = lastLocation
            Timber.i("Location update $lastLocation")
            // Copy to separate list as single use listeners may remove themselves while iterating
            listeners.toList().forEach { it.locationUpdate(lastLocation) }
        }
    }

    override fun onLocationAvailability(la: LocationAvailability?) {
        super.onLocationAvailability(la)
        la?.apply { listeners.toList().forEach { if (isLocationAvailable) it.locationAvailable() else it.locationUnavailable() } }
    }

    override fun lastLocationUpdate(): Location? = lastLocation

    override fun start() {
        try {
            val req = LocationRequest()
            req.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            req.interval = TimeUnit.SECONDS.toMillis(3)
            fusedLocationProviderClient.requestLocationUpdates(req, this, Looper.myLooper())
            fusedLocationProviderClient.lastLocation?.addOnCompleteListener {
                if (it.isSuccessful) lastLocation = it.result
            }
        } catch (se: SecurityException) {
            Timber.e(se, "Missing location permission")
        }
    }

    override fun stop() {
        fusedLocationProviderClient.removeLocationUpdates(this)
    }
}