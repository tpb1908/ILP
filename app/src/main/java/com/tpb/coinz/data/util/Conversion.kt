package com.tpb.coinz.data.util

import com.mapbox.mapboxsdk.geometry.LatLng
import com.tpb.coinz.data.coin.Coin
import com.tpb.coinz.data.coin.Currency
import com.tpb.coinz.data.users.User

object Conversion {

    fun toMap(coin: Coin): HashMap<String, Any> {
        return hashMapOf(
                "id" to coin.id,
                "value" to coin.value,
                "currency" to coin.currency.name,
                "markerSymbol" to coin.markerSymbol,
                "markerColor" to coin.markerColor,
                "latitude" to coin.location.latitude,
                "longitude" to coin.location.longitude,
                "banked" to coin.banked,
                "received" to coin.received)

    }

    fun toMap(user: User): HashMap<String, Any> {
        return hashMapOf(
                "uid" to user.uid,
                "email" to user.email
        )
    }

    fun fromMap(map: Map<String, Any>): Coin {
        return Coin(map["id"] as String, map["value"] as Double, Currency.fromString(map["currency"] as String),
                (map["markerSymbol"] as Long).toInt(), (map["markerColor"] as Long).toInt(),
                latLngFromMap(map),
                map["banked"] as Boolean, map["received"] as Boolean)
    }

    private fun latLngFromMap(map: Map<String, Any>): LatLng {
        return (if (map.containsKey("location")) map["location"] as Map<String, Any> else map).run {
            LatLng(this["latitude"] as Double, this["longitude"] as Double)
        }
    }

}