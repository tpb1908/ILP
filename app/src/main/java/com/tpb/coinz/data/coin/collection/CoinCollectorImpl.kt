package com.tpb.coinz.data.coin.collection

import android.location.Location
import com.mapbox.mapboxsdk.geometry.LatLng
import com.tpb.coinz.data.coin.Coin
import com.tpb.coinz.data.coin.Map
import com.tpb.coinz.data.coin.loading.MapLoader
import com.tpb.coinz.data.coin.storage.MapStore
import com.tpb.coinz.data.config.ConfigProvider
import com.tpb.coinz.data.location.LocationListener
import com.tpb.coinz.data.location.LocationProvider
import com.tpb.coinz.data.users.UserCollection
import timber.log.Timber
import java.util.*

class CoinCollectorImpl(private val lp: LocationProvider,
                        private val mapLoader: MapLoader,
                        private val mapStore: MapStore,
                        private val config: ConfigProvider,
                        private val coinCollection: CoinCollection,
                        private val userCollection: UserCollection) :
        CoinCollector, LocationListener.SimpleLocationListener {

    private var map: Map? = null

    private val listeners: MutableSet<CoinCollector.CoinCollectorListener> = hashSetOf()


    override fun loadMap() {
        lp.addListener(this)
        if (map?.isValidForDay(Calendar.getInstance()) == true) return // We already have a valid map
        mapStore.getLatest { result ->
            result.onSuccess {
                if (it.isValidForDay(Calendar.getInstance())) {
                    map = it
                    listeners.forEach { l -> l.mapLoaded(it) }
                    Timber.i("Coins loaded from store. Remaining coins: ${it.remainingCoins.size}")
                } else {
                    loadFromNetwork()
                }
            }.onFailure { loadFromNetwork() }
        }
    }

    private fun loadFromNetwork() {
        Timber.i("Loading coins from network")
        mapLoader.loadCoins(Calendar.getInstance()) { result ->
            result.onSuccess {
                map = it
                mapStore.store(it)
                listeners.forEach { l -> l.mapLoaded(it) }
            }.onFailure {
                //TODO: BEtter handling here
                listeners.forEach { l -> l.notifyReloading() }
                loadFromNetwork()
            }
        }
    }

    override fun addCollectionListener(listener: CoinCollector.CoinCollectorListener) {
        listeners.add(listener)
        map?.let { listener.mapLoaded(it) }
    }

    override fun removeCollectionListener(listener: CoinCollector.CoinCollectorListener) {
        listeners.remove(listener)
    }

    override fun dispose() {
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
            coin.location.distanceTo(LatLng(location.latitude, location.longitude)) < config.collectionDistance


    private fun collect(collectable: List<Coin>) {
        if (collectable.isNotEmpty()) {
            Timber.i("Collecting coins $collectable from map ${map?.collectedCoins?.size}")

            coinCollection.collectCoins(userCollection.getCurrentUser(), collectable) { result ->
                result.onSuccess { collected ->
                    map?.let {
                        Timber.i("Map prior to update ${it.remainingCoins.size}")
                        it.remainingCoins.removeAll(collected)
                        it.collectedCoins.addAll(collected)
                        mapStore.update(it)
                        listeners.forEach { l -> l.coinsCollected(collected) }
                    }
                }
            }
        }
    }

}
