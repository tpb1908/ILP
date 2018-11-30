package com.tpb.coinz.data.coin

import com.mapbox.mapboxsdk.geometry.LatLng

data class Coin(val id: String,
                val value: Double,
                val currency: Currency,
                val markerSymbol: Int,
                val markerColor: Int,
                val location: LatLng,
                val banked: Boolean = false,
                val received: Boolean = false)

