package com.tpb.coinz.data.coins

import android.location.Location
import com.mapbox.mapboxsdk.geometry.LatLng
import com.tpb.coinz.Result
import com.tpb.coinz.data.backend.collectionDistance
import com.tpb.coinz.data.location.LocationListener
import com.tpb.coinz.data.location.LocationProvider
import timber.log.Timber
import java.lang.Exception
import java.util.*

class CoinCollector(var lp: LocationProvider, private val coinLoader: CoinLoader, private val mapStore: MapStore) : LocationListener {

    private var map: Map? = null

    private val listeners: MutableSet<CoinCollectorListener> = hashSetOf()

    fun loadMap() {
        mapStore.getLatest { result ->
            if (result is Result.Value<Map> && result.v.isValidForDay(Calendar.getInstance())) {
                map = result.v
                listeners.forEach { it.mapLoaded(result.v) }
                Timber.i("Coins loaded from room. Remaining coins: ${result.v.remainingCoins}")
            } else {
                Timber.i("Loading coins from network")
                coinLoader.loadCoins(Calendar.getInstance()) { m ->
                    m?.let {
                        map = it
                        mapStore.store(it)
                        listeners.forEach { l -> l.mapLoaded(it) }
                    }
                }
            }
        }
    }


    fun addCollectionListener(listener: CoinCollectorListener) = listeners.add(listener)

    fun removeCollectionListener(listener: CoinCollectorListener) = listeners.remove(listener)


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
            map?.let {
                it.remainingCoins.removeAll(collectable)
                it.collectedCoins.addAll(collectable)
                mapStore.update(it)
            }
            listeners.forEach { it.coinsCollected(collectable) }
        }
    }

    override fun locationAvailable() {

    }

    override fun locationUnavailable() {
    }

    override fun locationUpdateError(exception: Exception) {
    }

    interface CoinCollectorListener {

        fun coinsCollected(collected: List<Coin>)

        fun mapLoaded(map: Map)

    }

}