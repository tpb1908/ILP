package com.tpb.coinz.map

import android.app.Application
import android.graphics.Color
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.mapbox.mapboxsdk.annotations.Icon
import com.mapbox.mapboxsdk.annotations.IconFactory
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.tpb.coinz.R
import com.tpb.coinz.base.BaseViewModel
import com.tpb.coinz.data.Coin
import com.tpb.coinz.data.CoinDownloader
import com.tpb.coinz.data.Map
import java.util.*

class MapViewModel(application: Application) : BaseViewModel<MapNavigator>(application) {

    val coins = MutableLiveData<List<MarkerOptions>>()

    private val iconFactory = IconFactory.getInstance(getApplication())

    override fun init() {
        CoinDownloader(mapLoadCallback).execute(Calendar.getInstance())
    }

    private val mapLoadCallback = { map: Map? ->
        coins.postValue(map?.coins?.map {
            MarkerOptions()
                    .position(it.location)
                    .title(it.currency.name)
                    .snippet(it.value.toString())
                    .setIcon(getCoinIcon(it))
        })
    }

    private fun getCoinIcon(coin: Coin): Icon {
        val bitmap = Utils.loadAndTintBitMap(getApplication(), R.drawable.ic_location_white_24dp, coin.markerColor)
        return iconFactory.fromBitmap(bitmap)
    }

}