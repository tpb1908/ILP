package com.tpb.coinz.data.coins

import android.util.Log
import com.mapbox.mapboxsdk.geometry.LatLng

data class Coin(val id: String, val value: Double, val currency: Currency, val markerSymbol: Int, val markerColor: Int, val location: LatLng) {

    fun toMap(): HashMap<String, Any> {
        return hashMapOf(id to hashMapOf(
                "value" to value,
                "currency" to currency.name,
                "markerSymbol" to markerSymbol,
                "markerColor" to markerColor,
                "latitude" to location.latitude,
                "longitude" to location.longitude))

    }

    companion object {
        fun fromMap(id: String, map: MutableMap<String, Any>): Coin {
            return Coin(id, map["value"] as Double, Currency.fromString(map["currency"] as String),
                    (map["markerSymbol"] as Long).toInt(), (map["markerColor"] as Long).toInt(),
                    LatLng(map["latitude"] as Double, map["longitude"] as Double))
        }
    }

}

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