package com.tpb.coinz.map

import android.app.Application
import android.location.Location
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.mapbox.mapboxsdk.annotations.Marker
import com.mapbox.mapboxsdk.geometry.LatLng
import com.tpb.coinz.App
import com.tpb.coinz.base.BaseViewModel
import com.tpb.coinz.data.ConnectionLiveData
import com.tpb.coinz.data.coins.Coin
import com.tpb.coinz.data.coins.CoinLoader
import com.tpb.coinz.data.coins.Map
import com.tpb.coinz.data.coins.MapStore
import com.tpb.coinz.data.location.LocationProvider
import com.tpb.coinz.db.collected
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
    private val store: FirebaseFirestore = FirebaseFirestore.getInstance()

    private var map: Map? = null
    private var markers: MutableMap<Coin, Marker> = HashMap()

    override fun init() {
        (getApplication() as App).mapComponent.inject(this)
        mapStore.getLatest {
            if(it?.dateGenerated?.before(Calendar.getInstance().time) == true) {
                coinLoader.loadCoins(Calendar.getInstance(), mapLoadCallback)
            } else {
                map = it
            }
        }
        locationProvider.addListener(this)
    }

    private val mapLoadCallback = { m: Map? ->
        if (m != null) {
            map = m
            mapStore.store(m)
            coins.postValue(m.coins)
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
            if (markers.isEmpty()) {
                //TODO: Notification of all coins collected
            }
        } else {
            Log.e("MapViewModel", "No marker for $coin")
        }
        store.collection(collected).document(user?.uid ?: "").set(coin, SetOptions.merge())
    }

}