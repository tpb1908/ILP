package com.tpb.coinz.home

import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.mapbox.mapboxsdk.annotations.Marker
import com.tpb.coinz.base.BaseViewModel
import com.tpb.coinz.data.backend.CoinCollection
import com.tpb.coinz.data.backend.UserCollection
import com.tpb.coinz.data.coins.Coin
import com.tpb.coinz.data.coins.CoinCollector
import com.tpb.coinz.data.coins.Map
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class HomeViewModel : BaseViewModel<HomeViewModel.HomeAction>(), CoinCollector.CoinCollectorListener {

    val coins = MutableLiveData<List<Coin>>()
    private var markers: MutableMap<Coin, Marker> = HashMap()

    @Inject
    lateinit var coinCollection: CoinCollection

    private var fbUser: FirebaseUser? = null

    @Inject
    lateinit var coinCollector: CoinCollector

    @Inject
    lateinit var userCollection: UserCollection

    val user = MutableLiveData<FirebaseUser>()

    val collectionInfo = MutableLiveData<MapInfo>()

    override val actions: MutableLiveData<HomeAction> = MutableLiveData()

    override fun bind() {
        fbUser = FirebaseAuth.getInstance().currentUser
        Timber.i("User $fbUser")
        if (fbUser == null) {
            actions.postValue(HomeAction.BeginLoginFlow)
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
                //actions.postValue(MapViewModel.MapAction.RemoveMarker(markers.getValue(coin)))
                markers.remove(coin)

            } else {
                Timber.e("No marker for $coin")
            }
            //TODO: Probably not the best way to do this
            collectionInfo.postValue(MapInfo(markers.size, 50-markers.size))
            coinCollection.collectCoin(userCollection.getCurrentUser(), coin)
        }
    }

    override fun mapLoaded(map: Map) {
        coins.postValue(map.remainingCoins)
    }

    fun mapMarkers(markers: MutableMap<Coin, Marker>) {
        this.markers = markers
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
        actions.postValue(HomeAction.BeginLoginFlow)
    }

    override fun onCleared() {
        super.onCleared()
        coinCollector.removeCollectionListener(this)
        coinCollector.dispose()
    }

    sealed class HomeAction {
        object BeginLoginFlow : HomeAction()
        data class RemoveMarker(val marker: Marker) : HomeAction()
    }


}