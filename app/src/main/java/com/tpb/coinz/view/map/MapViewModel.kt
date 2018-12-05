package com.tpb.coinz.view.map

import androidx.annotation.StringRes
import androidx.lifecycle.MutableLiveData
import com.mapbox.mapboxsdk.annotations.Marker
import com.tpb.coinz.R
import com.tpb.coinz.data.coin.Coin
import com.tpb.coinz.data.coin.CoinCollector
import com.tpb.coinz.data.coin.Map
import com.tpb.coinz.data.coin.collection.CoinCollection
import com.tpb.coinz.data.users.UserCollection
import com.tpb.coinz.view.base.ActionLiveData
import com.tpb.coinz.view.base.BaseViewModel
import timber.log.Timber

// There is no need to delay heavy instantiations here as
// the constructor injected values have already been instantiated for HomeViewModel
class MapViewModel(val coinCollection: CoinCollection,
                   val userCollection: UserCollection,
                   val coinCollector: CoinCollector) : BaseViewModel<MapViewModel.MapAction>(), CoinCollector.CoinCollectorListener {

    val coins = MutableLiveData<List<Coin>>()


    private var markers: MutableMap<Coin, Marker> = HashMap()

    override val actions = ActionLiveData<MapAction>()

    override fun bind() {
        coinCollector.setCoinCollection(coinCollection, userCollection.getCurrentUser())
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
                actions.postValue(MapAction.RemoveMarker(markers.getValue(coin)))
                markers.remove(coin)

                if (markers.isEmpty()) {
                    actions.postValue(MapAction.DisplayMessage(R.string.message_all_coins_collected))
                }
            } else {
                Timber.e("No marker for $coin")
            }
        }
    }

    override fun mapLoaded(map: Map) {
        coins.postValue(map.remainingCoins)
    }

    override fun notifyReloading() {
        actions.postValue(MapAction.ClearMarkers)
        markers.clear()
        coins.postValue(emptyList())
    }

    fun mapMarkers(markers: MutableMap<Coin, Marker>) {
        this.markers = markers
    }


    sealed class MapAction {
        class RemoveMarker(val marker: Marker) : MapAction()
        class DisplayMessage(@StringRes val message: Int) : MapAction()
        object ClearMarkers : MapAction()
    }
}