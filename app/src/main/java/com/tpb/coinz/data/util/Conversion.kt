package com.tpb.coinz.data.util

import com.google.firebase.firestore.FirebaseFirestoreException
import com.mapbox.mapboxsdk.geometry.LatLng
import com.tpb.coinz.data.coin.Coin
import com.tpb.coinz.data.coin.Currency

object Conversion {

    fun convertFireStoreException(fe: FirebaseFirestoreException?): CoinzException {
        if (fe == null) return CoinzException.UnknownException()
        return when (fe.code) {
            FirebaseFirestoreException.Code.OK, FirebaseFirestoreException.Code.INTERNAL, FirebaseFirestoreException.Code.RESOURCE_EXHAUSTED,
            FirebaseFirestoreException.Code.UNIMPLEMENTED, FirebaseFirestoreException.Code.UNKNOWN -> CoinzException.UnknownException()
            FirebaseFirestoreException.Code.NOT_FOUND -> CoinzException.NotFoundException()
            FirebaseFirestoreException.Code.ALREADY_EXISTS -> CoinzException.AlreadyExistsException()
            FirebaseFirestoreException.Code.INVALID_ARGUMENT, FirebaseFirestoreException.Code.FAILED_PRECONDITION, FirebaseFirestoreException.Code.OUT_OF_RANGE -> CoinzException.InvalidArgumentException()
            FirebaseFirestoreException.Code.PERMISSION_DENIED, FirebaseFirestoreException.Code.UNAUTHENTICATED -> CoinzException.AuthenticationException()
            FirebaseFirestoreException.Code.CANCELLED, FirebaseFirestoreException.Code.ABORTED -> CoinzException.CancelledException()
            FirebaseFirestoreException.Code.UNAVAILABLE, FirebaseFirestoreException.Code.DATA_LOSS, FirebaseFirestoreException.Code.DEADLINE_EXCEEDED -> CoinzException.NetworkException()
        }
    }

    fun toMap(coin: Coin): HashMap<String, Any> {
        return hashMapOf(
                "id" to coin.id,
                "value" to coin.value,
                "currency" to coin.currency.name,
                "markerSymbol" to coin.markerSymbol,
                "markerColor" to coin.markerColor,
                "latitude" to coin.location.latitude,
                "longitude" to coin.location.longitude,
                "received" to coin.received)

    }


    fun fromMap(map: Map<String, Any>): Coin {
        return Coin(map["id"] as String, map["value"] as Double, Currency.fromString(map["currency"] as String),
                (map["markerSymbol"] as Long).toInt(), (map["markerColor"] as Long).toInt(),
                latLngFromMap(map),
                map["received"] as Boolean)
    }

    private fun latLngFromMap(map: Map<String, Any>): LatLng {
        return (if (map.containsKey("location")) map["location"] as Map<String, Any> else map).run {
            LatLng(this["latitude"] as Double, this["longitude"] as Double)
        }
    }

}