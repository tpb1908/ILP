package com.tpb.coinz.map

import android.location.Location
import androidx.annotation.StringRes
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.mapbox.mapboxsdk.annotations.Marker
import com.mapbox.mapboxsdk.geometry.LatLng
import com.tpb.coinz.R
import com.tpb.coinz.Result
import com.tpb.coinz.base.BaseViewModel
import com.tpb.coinz.data.backend.CoinCollection
import com.tpb.coinz.data.backend.UserCollection
import com.tpb.coinz.data.coins.Coin
import com.tpb.coinz.data.coins.CoinLoader
import com.tpb.coinz.data.coins.Map
import com.tpb.coinz.data.coins.MapStore
import com.tpb.coinz.data.location.LocationProvider
import com.tpb.coinz.data.backend.collectionDistance
import timber.log.Timber
import java.lang.Exception
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashMap

class MapViewModel : BaseViewModel<MapViewModel.MapActions>(), com.tpb.coinz.data.location.LocationListener {

    @Inject
    lateinit var coinLoader: CoinLoader
    val coins = MutableLiveData<List<Coin>>()

    @Inject
    lateinit var mapStore: MapStore

    @Inject
    lateinit var locationProvider: LocationProvider


    @Inject lateinit var coinCollection: CoinCollection

    @Inject lateinit var userCollection: UserCollection

    private var map: Map? = null
    private var markers: MutableMap<Coin, Marker> = HashMap()

    override val actions = MutableLiveData<MapActions>()

    override fun bind() {
        mapStore.getLatest {
            if (it is Result.Value<Map> && it.v.isValidForDay(Calendar.getInstance())) {
                map = it.v
                Timber.i("Coins loaded from room")
                coins.postValue(it.v.remainingCoins)
            } else {
                Timber.i("Loading coins from network")
                coinLoader.loadCoins(Calendar.getInstance(), mapLoadCallback)
            }
        }
        locationProvider.addListener(this)
    }

    override fun onCleared() {
        super.onCleared()
        locationProvider.removeListener(this)
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
        Timber.i("Collecting coin $coin")
        if (markers.containsKey(coin)) {
            actions.postValue(MapActions.RemoveMarker(markers.getValue(coin)))
            markers.remove(coin)
            map?.let {
                it.remainingCoins.remove(coin)
                it.collectedCoins.add(coin)
                mapStore.update(it)
            }
            if (markers.isEmpty()) {
                actions.postValue(MapActions.DisplayMessage(R.string.message_all_coins_collected))
            }
        } else {
            Timber.e("No marker for $coin")
        }
        //TODO: Cleanup. Error if user null
        coinCollection.collectCoin(userCollection.getCurrentUser().uid, coin)
    }

    sealed class MapActions {
        class RemoveMarker(val marker: Marker): MapActions()
        class DisplayMessage(@StringRes val message: Int): MapActions()
    }
}