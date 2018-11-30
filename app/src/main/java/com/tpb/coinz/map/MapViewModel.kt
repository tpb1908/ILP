package com.tpb.coinz.map

import androidx.annotation.StringRes
import androidx.lifecycle.MutableLiveData
import com.mapbox.mapboxsdk.annotations.Marker
import com.tpb.coinz.R
import com.tpb.coinz.base.BaseViewModel
import com.tpb.coinz.data.backend.CoinCollection
import com.tpb.coinz.data.backend.UserCollection
import com.tpb.coinz.data.coins.*
import com.tpb.coinz.data.coins.Map
import timber.log.Timber
import javax.inject.Inject
import kotlin.collections.HashMap

class MapViewModel : BaseViewModel<MapViewModel.MapActions>(), CoinCollector.CoinCollectorListener {

    val coins = MutableLiveData<List<Coin>>()

    @Inject
    lateinit var coinCollection: CoinCollection

    @Inject
    lateinit var userCollection: UserCollection

    private var markers: MutableMap<Coin, Marker> = HashMap()

    override val actions = MutableLiveData<MapActions>()

    @Inject
    lateinit var coinCollector: CoinCollector


    override fun bind() {
        coinCollector.addCollectionListener(this)
        coinCollector.loadMap()
    }

    override fun onCleared() {
        super.onCleared()
        coinCollector.removeCollectionListener(this)
        coinCollector.dispose()
    }

    override fun coinsCollected(collected: List<Coin>) {
        collected.forEach { coin ->
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
    }

    override fun mapLoaded(map: Map) {
        coins.postValue(map.remainingCoins)
    }


    fun mapMarkers(markers: MutableMap<Coin, Marker>) {
        this.markers = markers
    }


    sealed class MapActions {
        class RemoveMarker(val marker: Marker) : MapActions()
        class DisplayMessage(@StringRes val message: Int) : MapActions()
    }
}