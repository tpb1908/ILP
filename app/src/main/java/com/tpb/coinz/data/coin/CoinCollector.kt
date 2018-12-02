package com.tpb.coinz.data.coin

import android.location.Location
import com.mapbox.mapboxsdk.geometry.LatLng
import com.tpb.coinz.Result
import com.tpb.coinz.data.coin.collection.CoinCollection
import com.tpb.coinz.data.coin.loading.MapLoader
import com.tpb.coinz.data.coin.storage.MapStore
import com.tpb.coinz.data.location.LocationListener
import com.tpb.coinz.data.location.LocationProvider
import com.tpb.coinz.data.users.User
import timber.log.Timber
import java.util.*

class CoinCollector(private val lp: LocationProvider, private val mapLoader: MapLoader, private val mapStore: MapStore) : LocationListener {

    private var map: Map? = null

    private val listeners: MutableSet<CoinCollectorListener> = hashSetOf()

    private var coinCollection: CoinCollection? = null
    private var user: User? = null

    fun setCoinCollection(coinCollection: CoinCollection, user: User) {
        this.coinCollection = coinCollection
        this.user = user
    }

    fun loadMap() {
        lp.addListener(this)
        mapStore.getLatest { result ->
            // If a map exists, and is valid, we use it
            if (result is Result.Value<Map> && result.v.isValidForDay(Calendar.getInstance())) {
                map = result.v
                listeners.forEach { it.mapLoaded(result.v) }
                Timber.i("Coins loaded from room. Remaining coins: ${result.v.remainingCoins.size}")
            } else {
                loadFromNetwork()
            }
        }
    }

    private fun loadFromNetwork() {
        Timber.i("Loading coins from network")
        mapLoader.loadCoins(Calendar.getInstance()) { result ->
            if (result is Result.Value) {
                map = result.v
                mapStore.store(result.v)
                listeners.forEach { it.mapLoaded(result.v) }
            }
        }
    }

    fun addCollectionListener(listener: CoinCollectorListener) = listeners.add(listener)

    fun removeCollectionListener(listener: CoinCollectorListener) = listeners.remove(listener)


    fun dispose() {
        lp.removeListener(this)
    }


    override fun locationUpdate(location: Location) {
        map?.let { m ->
            if (m.isValidForDay(Calendar.getInstance())) {
                collect(m.remainingCoins.filter { collectable(it, location) })
            } else {
                map = null
                listeners.forEach { it.notifyReloading() }
                loadFromNetwork()

            }
        }
    }

    private fun collectable(coin: Coin, location: Location): Boolean =
            coin.location.distanceTo(LatLng(location.latitude, location.longitude)) < 25


    private fun collect(collectable: List<Coin>) {
        if (collectable.isNotEmpty()) {
            Timber.i("Collecting coins $collectable from map ${map?.collectedCoins?.size}")
            map?.let {
                Timber.i("Map prior to update ${it.remainingCoins.size}")
                it.remainingCoins.removeAll(collectable)
                it.collectedCoins.addAll(collectable)
                mapStore.update(it)
            }
            user?.let { user ->
                collectable.forEach { coinCollection?.collectCoin(user, it) }
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

        fun notifyReloading()

    }

}