package com.tpb.coinz.data.coins

import android.location.Location
import com.mapbox.mapboxsdk.geometry.LatLng
import com.tpb.coinz.data.backend.collectionDistance
import com.tpb.coinz.data.location.LocationListener
import com.tpb.coinz.data.location.LocationProvider
import timber.log.Timber
import java.lang.Exception

class CoinCollector(var lp: LocationProvider) : LocationListener {

    var map: Map? = null

    var coinCollectionListener: (List<Coin>) -> Unit = {}

    init {
        lp.addListener(this)
    }

    fun dispose() {
        lp.removeListener(this)
    }


    override fun locationUpdate(location: Location) {
        map?.let {map ->
            collect(map.remainingCoins.filter { collectable(it, location) })
        }
    }

    private fun collectable(coin: Coin, location: Location): Boolean =
            coin.location.distanceTo(LatLng(location.latitude, location.longitude)) < collectionDistance



    private fun collect(collectable: List<Coin>) {
        if (collectable.isNotEmpty()) {
            Timber.i("Collecting coins $collectable from map ${map?.collectedCoins?.size}")
            map?.remainingCoins?.removeAll(collectable)
            map?.collectedCoins?.addAll(collectable)
            coinCollectionListener(collectable)
        }
    }

    override fun locationAvailable() {

    }

    override fun locationUnavailable() {
    }

    override fun locationUpdateError(exception: Exception) {
    }
}