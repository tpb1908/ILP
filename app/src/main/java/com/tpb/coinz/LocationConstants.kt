package com.tpb.coinz

import com.mapbox.mapboxsdk.annotations.PolygonOptions
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.geometry.LatLngBounds

object LocationConstants {

    public val points = arrayListOf(LatLng(55.946233, -3.192473)
            ,LatLng(55.946233, -3.184319)
            ,LatLng(55.942617, -3.192473)
            ,LatLng(55.942617, -3.184319)
    )

    public val bounds = LatLngBounds.Builder().includes(points).build()

    public val polygon = PolygonOptions().addAll(points)
}

