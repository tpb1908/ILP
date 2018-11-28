package com.tpb.coinz.data.location

import android.annotation.SuppressLint
import android.location.Location
import com.mapbox.android.core.location.LocationEngine
import timber.log.Timber

class LocationListeningEngine(private val locationProvider: LocationProvider) : LocationEngine(), LocationListener {

    private var connected = false
    override fun activate() {
        locationProvider.addListener(this)
    }

    override fun deactivate() {
        locationProvider.removeListener(this)
    }

    override fun requestLocationUpdates() {
        locationProvider.start()
    }

    override fun removeLocationUpdates() {
        locationProvider.pause()
    }

    override fun isConnected(): Boolean = connected

    @SuppressLint("MissingPermission")
    override fun getLastLocation(): Location? = locationProvider.lastLocationUpdate()


    override fun obtainType(): Type {
        return LocationEngine.Type.GOOGLE_PLAY_SERVICES
    }

    override fun locationUpdate(location: Location) {
        Timber.i("LocationEngine impl location update $location")
    }

    override fun locationAvailable() {
        connected = true
    }

    override fun locationUnavailable() {
        connected = false
    }

    override fun locationUpdateError(exception: Exception) {}
}