package com.tpb.coinz.data.location.background

import android.app.IntentService
import android.content.Intent
import android.os.Build
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import com.tpb.coinz.BuildConfig
import timber.log.Timber

//https://developer.android.com/training/location/geofencing
class GeofenceTransitionsIntentService : IntentService(ServiceName) {

    companion object {
        const val ServiceName = BuildConfig.APPLICATION_ID + "GeoFence"
    }

    override fun onCreate() {
        super.onCreate()
        Timber.i("Geofence service started")
    }

    override fun onHandleIntent(intent: Intent?) {
        val event = GeofencingEvent.fromIntent(intent)
        if (event.hasError()) {
            Timber.e("GeoFence error code ${event.errorCode}")
            return
        }

        val transition = event.geofenceTransition
        if (transition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Timber.i("Starting foreground service")
                startForegroundService(Intent(this, ForegroundLocationService::class.java))
            } else {
                Timber.i("Starting service")
                startService(Intent(this, ForegroundLocationService::class.java))
            }
        } else if (transition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            Timber.i("Exiting Geofence area")
            stopService(Intent(this, ForegroundLocationService::class.java))
        }
    }
}