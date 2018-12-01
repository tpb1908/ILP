package com.tpb.coinz.data.coin

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.mapbox.mapboxsdk.geometry.LatLng
import com.tpb.coinz.data.users.User

abstract class FireStoreCoin(protected val store: FirebaseFirestore) {

    protected val collected = "collected"
    protected val coins = "coins"

    protected inline fun coins(user: User): CollectionReference = store.collection(collected).document(user.uid).collection(coins)

    protected fun toMap(coin: Coin): HashMap<String, Any> {
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

    protected fun fromMap(map: MutableMap<String, Any>): Coin {
        return Coin(map["id"] as String, map["value"] as Double, Currency.fromString(map["currency"] as String),
                (map["markerSymbol"] as Long).toInt(), (map["markerColor"] as Long).toInt(),
                LatLng(map["latitude"] as Double, map["longitude"] as Double),
                map["banked"] as Boolean, map["received"] as Boolean)
    }

}