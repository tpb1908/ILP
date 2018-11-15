package com.tpb.coinz.map

import androidx.annotation.IdRes
import androidx.annotation.StringRes
import com.mapbox.mapboxsdk.annotations.Marker
import com.mapbox.mapboxsdk.annotations.MarkerOptions

interface MapNavigator {


    fun removeMarker(marker: Marker)

    fun requestLocationPermission()

    fun beginLocationTracking()

    fun displayMessage(@StringRes resId: Int)
}