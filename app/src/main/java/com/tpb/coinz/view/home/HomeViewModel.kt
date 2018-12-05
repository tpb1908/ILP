package com.tpb.coinz.view.home

import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.mapbox.mapboxsdk.annotations.Marker
import com.tpb.coinz.data.chat.ChatCollection
import com.tpb.coinz.data.chat.Thread
import com.tpb.coinz.data.coin.Coin
import com.tpb.coinz.data.coin.CoinCollector
import com.tpb.coinz.data.coin.Map
import com.tpb.coinz.data.coin.collection.CoinCollection
import com.tpb.coinz.data.config.ConfigProvider
import com.tpb.coinz.data.users.UserCollection
import com.tpb.coinz.data.util.Registration
import com.tpb.coinz.view.base.ActionLiveData
import com.tpb.coinz.view.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import java.util.*
import kotlin.concurrent.thread


class HomeViewModel(val config: ConfigProvider,
                    val userCollection: UserCollection
                    ) : BaseViewModel<HomeViewModel.HomeAction>(), CoinCollector.CoinCollectorListener, KoinComponent {


    val coins = MutableLiveData<List<Coin>>()
    private var markers: MutableMap<Coin, Marker> = HashMap()


    private var fbUser: FirebaseUser? = null

    val user = MutableLiveData<FirebaseUser>()


    val collectionInfo = MutableLiveData<MapInfo>()

    val threads = MutableLiveData<List<Thread>>()
    private val allThreads = mutableListOf<Thread>()

    private var threadsRegistration: Registration? = null

    val coinCollection: CoinCollection by inject()
    val chatCollection: ChatCollection by inject()
    val coinCollector: CoinCollector by inject()

    override val actions = ActionLiveData<HomeAction>()

    override fun bind() {
        fbUser = FirebaseAuth.getInstance().currentUser
        Timber.i("User $fbUser")
        if (fbUser == null) {
            actions.postValue(HomeAction.BeginLoginFlow)
        } else {
            user.postValue(fbUser)
            initInBackground()
        }

    }

    private fun initInBackground() {
        GlobalScope.launch(Dispatchers.IO) {

            coinCollector.addCollectionListener(this@HomeViewModel)
            coinCollector.setCoinCollection(coinCollection, userCollection.getCurrentUser())
            coinCollector.loadMap()

            threadsRegistration = chatCollection.openRecentThreads(userCollection.getCurrentUser(), 10) {
                Timber.i("Received new threads $it")
                it.onSuccess { newThreads ->
                    Timber.i("Retrieved threads ${newThreads}")
                    allThreads.addAll(newThreads)
                    threads.postValue(allThreads)
                }
            }
        }
    }

    override fun coinsCollected(collected: List<Coin>) {
        collected.forEach { coin ->
            if (markers.containsKey(coin)) {
                Timber.i("Removing marker for $coin")
                actions.postValue(HomeAction.RemoveMarker(markers.getValue(coin)))
                markers.remove(coin)
            } else {
                Timber.e("No marker for $coin")
            }
            //TODO: Probably not the best way to do this
            collectionInfo.postValue(MapInfo(config.coinsPerMap - markers.size, markers.size))
        }
    }

    override fun mapLoaded(map: Map) {
        coins.postValue(map.remainingCoins)
        collectionInfo.postValue(MapInfo(map.collectedCoins.size, map.remainingCoins.size))
    }

    override fun notifyReloading() {
        actions.postValue(HomeAction.ClearMarkers)
        coins.postValue(emptyList())
        markers.clear()
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
        threadsRegistration?.deregister()
    }

    sealed class HomeAction {
        object BeginLoginFlow : HomeAction()
        object ClearMarkers : HomeAction()
        data class RemoveMarker(val marker: Marker) : HomeAction()
    }


}