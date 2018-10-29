package com.tpb.coinz.data

import android.util.Log
import com.mapbox.mapboxsdk.geometry.LatLng

data class Coin(val id: String, val value: Double, val currency: Currency,val markerSymbol: Int, val markerColor: Int, val location: LatLng)

enum class Currency {
    PENY, DOLR, SHIL, QUID;

    companion object {
        fun fromString(name: String): Currency {
            return try {
                valueOf(name)
            } catch (e: IllegalArgumentException) {
                Log.e("Currency", "Invalid currency name $name")
                PENY
            }
        }
    }
}