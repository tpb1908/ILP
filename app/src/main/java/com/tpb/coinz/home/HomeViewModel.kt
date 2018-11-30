package com.tpb.coinz.home

import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.mapbox.mapboxsdk.annotations.Marker
import com.tpb.coinz.R
import com.tpb.coinz.Result
import com.tpb.coinz.base.BaseViewModel
import com.tpb.coinz.data.backend.CoinCollection
import com.tpb.coinz.data.coins.*
import com.tpb.coinz.data.coins.Map
import com.tpb.coinz.map.MapViewModel
import dagger.Lazy
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class HomeViewModel : BaseViewModel<HomeViewModel.HomeActions>(), CoinCollector.CoinCollectorListener {

    val coins = MutableLiveData<List<Coin>>()

    private var markers: MutableMap<Coin, Marker> = HashMap()

    private var fbUser: FirebaseUser? = null

    @Inject
    lateinit var coinCollector: CoinCollector

    val user = MutableLiveData<FirebaseUser>()

    val collectionInfo = MutableLiveData<MapInfo>()

    override val actions: MutableLiveData<HomeActions> = MutableLiveData()

    override fun bind() {
        fbUser = FirebaseAuth.getInstance().currentUser
        Timber.i("User $fbUser")
        if (fbUser == null) {
            actions.postValue(HomeActions.BEGIN_LOGIN_FLOW)
        } else {
            user.postValue(fbUser)
            coinCollector.addCollectionListener(this)
            coinCollector.loadMap()
        }

    }

    override fun coinsCollected(collected: List<Coin>) {
        collected.forEach { coin ->
            if (markers.containsKey(coin)) {
                Timber.i("Removing marker for $coin")
                //actions.postValue(MapViewModel.MapActions.RemoveMarker(markers.getValue(coin)))
                markers.remove(coin)

            } else {
                Timber.e("No marker for $coin")
            }
            //TODO coinCollection.collectCoin(userCollection.getCurrentUser(), coin)
        }
    }

    override fun mapLoaded(map: Map) {
        coins.postValue(map.remainingCoins)
    }

    fun mapMarkers(markers: MutableMap<Coin, Marker>) {
        this.markers = markers
    }

    // If we haven't collected any remainingCoins
    private fun postEmptyCollectionInfo() {
        collectionInfo.postValue(MapInfo(0, 50))
    }

    private fun postCoinCollectionInfo(map: Map) {
        collectionInfo.postValue(MapInfo(map.collectedCoins.size, map.remainingCoins.size))
    }

    fun userLoggedIn() {
        fbUser = FirebaseAuth.getInstance().currentUser
        user.postValue(fbUser)
        //TODO: Clean this up and add proper error handling
        fbUser?.let {
            FirebaseFirestore.getInstance().collection("users").document(it.uid).set(
                    mapOf("email" to it.email)
            ).addOnCompleteListener { task ->
                Timber.i("Stored user email ${it.email}")
            }
        }

    }

    fun userLoginFailed() {
        actions.postValue(HomeActions.BEGIN_LOGIN_FLOW)
    }

    override fun onCleared() {
        super.onCleared()
        coinCollector.removeCollectionListener(this)
        coinCollector.dispose()
    }

    enum class HomeActions {
        BEGIN_LOGIN_FLOW
    }

}