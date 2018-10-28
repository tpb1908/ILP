package com.tpb.coinz

import android.location.Location
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng

fun Location.asCameraUpdate() = CameraUpdateFactory.newLatLngZoom(LatLng(this), 15.0)