package com.tpb.coinz.map

import androidx.annotation.StringRes
import androidx.lifecycle.MutableLiveData
import com.mapbox.mapboxsdk.annotations.Marker
import com.tpb.coinz.R
import com.tpb.coinz.Result
import com.tpb.coinz.base.BaseViewModel
import com.tpb.coinz.data.backend.CoinCollection
import com.tpb.coinz.data.backend.UserCollection
import com.tpb.coinz.data.location.LocationProvider
import com.tpb.coinz.data.coins.*
import com.tpb.coinz.data.coins.Map
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashMap

class MapViewModel : BaseViewModel<MapViewModel.MapActions>() {

    @Inject
    lateinit var coinLoader: CoinLoader
    val coins = MutableLiveData<List<Coin>>()

    @Inject
    lateinit var mapStore: MapStore


    @Inject
    lateinit var coinCollection: CoinCollection

    @Inject
    lateinit var userCollection: UserCollection

    private var map: Map? = null
    private var markers: MutableMap<Coin, Marker> = HashMap()

    override val actions = MutableLiveData<MapActions>()

    @Inject
    lateinit var coinCollector: CoinCollector


    override fun bind() {
        coinCollector.coinCollectionListener = this::collect
        mapStore.getLatest {
            if (it is Result.Value<Map> && it.v.isValidForDay(Calendar.getInstance())) {
                map = it.v
                coinCollector.map = map
                Timber.i("Coins loaded from room")
                coins.postValue(it.v.remainingCoins)
            } else {
                Timber.i("Loading coins from network")
                coinLoader.loadCoins(Calendar.getInstance(), mapLoadCallback)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        coinCollector.dispose()
    }

    private val mapLoadCallback = { m: Map? ->
        if (m != null) {
            map = m
            mapStore.store(m)
            coinCollector.map = m
            coins.postValue(m.remainingCoins)
        }
    }

    private fun collect(collectable: List<Coin>) {
        collectable.forEach { coin ->
            if (markers.containsKey(coin)) {
                Timber.i("Removing marker for $coin")
                actions.postValue(MapActions.RemoveMarker(markers.getValue(coin)))
                markers.remove(coin)

                if (markers.isEmpty()) {
                    actions.postValue(MapActions.DisplayMessage(R.string.message_all_coins_collected))
                }
            } else {
                Timber.e("No marker for $coin")
            }
            coinCollection.collectCoin(userCollection.getCurrentUser(), coin)
        }
        map?.let {
            Timber.i("Updating map ${it.collectedCoins.size}")
            mapStore.update(it)
        }
    }

    fun mapMarkers(markers: MutableMap<Coin, Marker>) {
        this.markers = markers
    }


    sealed class MapActions {
        class RemoveMarker(val marker: Marker) : MapActions()
        class DisplayMessage(@StringRes val message: Int) : MapActions()
    }
}