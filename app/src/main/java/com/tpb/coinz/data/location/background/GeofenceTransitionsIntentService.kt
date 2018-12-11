package com.tpb.coinz.data.location.background

import android.app.IntentService
import android.content.Intent
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import com.tpb.coinz.BuildConfig
import timber.log.Timber

//https://developer.android.com/training/location/geofencing
/**
 * IntentService to receive GeoFence transition events from Google Play services
 */
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
            // ForegroundLocationService handles foreground notification requirements for different API levels
            ForegroundLocationService.start(this)
        } else if (transition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            Timber.i("Exiting Geofence area")
            stopService(Intent(this, ForegroundLocationService::class.java))
        }
    }
}