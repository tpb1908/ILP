package com.tpb.coinz.map

import android.app.Application
import android.graphics.Color
import android.location.Location
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.mapbox.android.core.location.LocationEngineListener
import com.mapbox.android.core.location.LocationEngineProvider
import com.mapbox.mapboxsdk.annotations.Icon
import com.mapbox.mapboxsdk.annotations.IconFactory
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.geometry.LatLng
import com.tpb.coinz.App
import com.tpb.coinz.LocationListener
import com.tpb.coinz.R
import com.tpb.coinz.base.BaseViewModel
import com.tpb.coinz.data.Coin
import com.tpb.coinz.data.CoinDownloader
import com.tpb.coinz.data.CoinLoader
import com.tpb.coinz.data.Map
import com.tpb.coinz.data.location.LocationProvider
import com.tpb.coinz.db.collected
import com.tpb.coinz.db.collectionDistance
import java.lang.Exception
import java.util.*
import javax.inject.Inject

class MapViewModel(application: Application) : BaseViewModel<MapNavigator>(application), com.tpb.coinz.data.location.LocationListener {

    @Inject lateinit var coinLoader: CoinLoader
    val coins = MutableLiveData<List<MarkerOptions>>()

    @Inject lateinit var locationProvider: LocationProvider

    private val iconFactory = IconFactory.getInstance(getApplication())

    private val user = FirebaseAuth.getInstance().currentUser
    private val store: FirebaseFirestore = FirebaseFirestore.getInstance()

    private var map: Map? = null

    override fun init() {
        (getApplication() as App).mapComponent.inject(this)
        coinLoader.loadCoins(Calendar.getInstance(), mapLoadCallback)
        locationProvider.addListener(this)


    }

    private val mapLoadCallback = { m: Map? ->
        map = m
        coins.postValue(m?.coins?.map(::coinToMarkerOption))
    }

    private fun coinToMarkerOption(coin: Coin): MarkerOptions {
        return MarkerOptions()
                .position(coin.location)
                .title(coin.currency.name)
                .snippet(coin.value.toString())
                .setIcon(getCoinIcon(coin))
    }

    private fun getCoinIcon(coin: Coin): Icon {
        val bitmap = Utils.loadAndTintBitMap(getApplication(), R.drawable.ic_location_white_24dp, coin.markerColor)
        return iconFactory.fromBitmap(bitmap)
    }

    override fun locationUpdate(location: Location) {
        map?.coins?.forEach {
            if (it.location.distanceTo(LatLng(location.latitude, location.longitude)) < collectionDistance) {
                collect(it)
            }
        }
    }

    override fun locationAvailable() {
    }

    override fun locationUnavailable() {
    }

    override fun locationUpdateError(exception: Exception) {
    }


    private fun collect(coin: Coin) {
        Log.i("MapViewModel", "Collecting coin $coin")
        store.collection(collected).document(user?.uid ?: "").set(coin, SetOptions.merge())
    }

}