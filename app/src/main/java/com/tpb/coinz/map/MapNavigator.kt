package com.tpb.coinz.map

import com.mapbox.mapboxsdk.annotations.Marker
import com.mapbox.mapboxsdk.annotations.MarkerOptions

interface MapNavigator {


    fun removeMarker(marker: Marker)

    fun requestLocationPermission()

    fun beginLocationTracking()

}