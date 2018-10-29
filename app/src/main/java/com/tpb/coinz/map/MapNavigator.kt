package com.tpb.coinz.map

import com.mapbox.mapboxsdk.annotations.Marker
import com.mapbox.mapboxsdk.annotations.MarkerOptions

interface MapNavigator {

    fun addMarkers(markers: List<MarkerOptions>, callback: (List<Marker>) -> Unit)

}