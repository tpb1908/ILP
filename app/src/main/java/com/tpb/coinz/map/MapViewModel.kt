package com.tpb.coinz.map

import android.app.Application
import android.location.Location
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.mapbox.mapboxsdk.annotations.Marker
import com.mapbox.mapboxsdk.geometry.LatLng
import com.tpb.coinz.App
import com.tpb.coinz.Result
import com.tpb.coinz.base.BaseViewModel
import com.tpb.coinz.data.backend.CoinCollection
import com.tpb.coinz.data.coins.Coin
import com.tpb.coinz.data.coins.CoinLoader
import com.tpb.coinz.data.coins.Map
import com.tpb.coinz.data.coins.MapStore
import com.tpb.coinz.data.location.LocationProvider
import com.tpb.coinz.db.collectionDistance
import java.lang.Exception
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashMap

class MapViewModel(application: Application) : BaseViewModel<MapNavigator>(application), com.tpb.coinz.data.location.LocationListener {

    @Inject
    lateinit var coinLoader: CoinLoader
    val coins = MutableLiveData<List<Coin>>()

    @Inject
    lateinit var mapStore: MapStore

    @Inject
    lateinit var locationProvider: LocationProvider

    private val user = FirebaseAuth.getInstance().currentUser

    @Inject lateinit var coinCollection: CoinCollection

    private var map: Map? = null
    private var markers: MutableMap<Coin, Marker> = HashMap()

    override fun init() {
        (getApplication() as App).mapComponent.inject(this)
        mapStore.getLatest {
            if (it is Result.Value<Map> && it.v.isValidForDay(Calendar.getInstance())) {
                map = it.v
                Log.i("MapViewModel", "Coins loaded from room")
                coins.postValue(it.v.remainingCoins)
            } else {
                Log.i("MapViewModel", "Loading remainingCoins")
                coinLoader.loadCoins(Calendar.getInstance(), mapLoadCallback)
            }
        }
        locationProvider.addListener(this)
    }

    private val mapLoadCallback = { m: Map? ->
        if (m != null) {
            map = m
            mapStore.store(m)
            coins.postValue(m.remainingCoins)
        }
    }


    override fun locationUpdate(location: Location) {
        markers.keys.filter { collectible(it, location) }.forEach { collect(it) }
    }

    private fun collectible(coin: Coin, location: Location): Boolean =
            coin.location.distanceTo(LatLng(location.latitude, location.longitude)) < collectionDistance


    override fun locationAvailable() {

    }

    override fun locationUnavailable() {
    }

    override fun locationUpdateError(exception: Exception) {
    }

    fun mapMarkers(markers: MutableMap<Coin, Marker>) {
        this.markers = markers
    }

    private fun collect(coin: Coin) {
        Log.i("MapViewModel", "Collecting coin $coin")
        if (markers.containsKey(coin)) {
            navigator.get()?.removeMarker(markers.getValue(coin))
            markers.remove(coin)
            map?.let {
                it.remainingCoins.remove(coin)
                it.collectedCoins.add(coin)
                mapStore.update(it)
            }
            if (markers.isEmpty()) {
                //TODO: Notification of all remainingCoins collected
            }
        } else {
            Log.e("MapViewModel", "No marker for $coin")
        }
        //TODO: Cleanup
        coinCollection.collectCoin(user?.uid ?: "", coin)
    }

}