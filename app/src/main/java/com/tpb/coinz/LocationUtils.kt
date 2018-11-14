package com.tpb.coinz

import android.app.Activity
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.mapboxsdk.annotations.PolygonOptions
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.geometry.LatLngBounds

object LocationUtils {

    public val points = arrayListOf(LatLng(55.946233, -3.192473)
            ,LatLng(55.946233, -3.184319)
            ,LatLng(55.942617, -3.192473)
            ,LatLng(55.942617, -3.184319)
    )

    public val bounds = LatLngBounds.Builder().includes(points).build()

    public val polygon = PolygonOptions().addAll(points)

    public fun requestPermission(activity: Activity, permissionsListener: PermissionsListener) {
        if (PermissionsManager.areLocationPermissionsGranted(activity)) {
            val manager = PermissionsManager(permissionsListener)
            manager.requestLocationPermissions(activity)
        }


    }
}

