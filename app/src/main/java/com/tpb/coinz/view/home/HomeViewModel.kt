package com.tpb.coinz.view.home

import androidx.lifecycle.MutableLiveData
import com.mapbox.mapboxsdk.annotations.Marker
import com.tpb.coinz.data.chat.ChatCollection
import com.tpb.coinz.data.chat.Thread
import com.tpb.coinz.data.coin.Coin
import com.tpb.coinz.data.coin.Map
import com.tpb.coinz.data.coin.Transaction
import com.tpb.coinz.data.coin.bank.CoinBank
import com.tpb.coinz.data.coin.collection.CoinCollection
import com.tpb.coinz.data.coin.collection.CoinCollector
import com.tpb.coinz.data.config.ConfigProvider
import com.tpb.coinz.data.users.User
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


class HomeViewModel(val config: ConfigProvider,
                    private val userCollection: UserCollection
                    ) : BaseViewModel<HomeViewModel.HomeAction>(), com.tpb.coinz.data.coin.collection.CoinCollector.CoinCollectorListener, KoinComponent {


    override val actions = ActionLiveData<HomeAction>()

    val collectionInfo = MutableLiveData<MapInfo>()
    val coins = MutableLiveData<List<Coin>>()
    private var markers: MutableMap<Coin, Marker> = HashMap()

    val user = MutableLiveData<User>()
    val threads = MutableLiveData<List<Thread>>()
    private val allThreads = mutableListOf<Thread>()

    val bankInfo = MutableLiveData<BankInfo>()
    val recentlyBanked = MutableLiveData<List<Transaction>>()

    private val chatCollection: ChatCollection by inject()
    private var threadsRegistration: Registration? = null
    private val coinCollection: CoinCollection by inject()
    private val coinCollector: CoinCollector by inject()
    private var bankRegistration: Registration? = null
    private val coinBank: CoinBank by inject()

    override fun bind() {
        if (userCollection.isSignedIn()) {
            user.postValue(userCollection.getCurrentUser())
            initInBackground()
        } else {
            actions.postValue(HomeAction.BeginLoginFlow)
        }

    }

    private fun initInBackground() {
        GlobalScope.launch(Dispatchers.IO) {

            coinCollector.addCollectionListener(this@HomeViewModel)
            coinCollector.setCoinCollection(coinCollection, userCollection.getCurrentUser())
            coinCollector.loadMap()

            if (threadsRegistration == null) {
                threadsRegistration = chatCollection.openRecentThreads(userCollection.getCurrentUser(), 10) {
                    Timber.i("Received new threads $it")
                    it.onSuccess { newThreads ->
                        Timber.i("Retrieved threads $newThreads")
                        allThreads.addAll(newThreads)
                        threads.postValue(allThreads)
                    }
                }
            }
            postBankedInfo()
            if (bankRegistration == null) {
                bankRegistration = coinBank.getRecentlyBankedCoins(userCollection.getCurrentUser(), 10) {
                    it.onSuccess { rb ->
                        Timber.i("Recently banked coins $rb")
                        recentlyBanked.postValue(rb)
                        postBankedInfo()
                    }
                }
            }
        }
    }

    private fun postBankedInfo() {
        val numBankable = coinBank.getNumBankable()
        Timber.i("Updating bank info. Still bankable: $numBankable")
        bankInfo.postValue(BankInfo(config.dailyBankLimit-numBankable, numBankable))
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


    fun userLoggedIn(id: String, email: String) {
        userCollection.createUser(id, email) {
            user.postValue(userCollection.getCurrentUser())
            initInBackground()
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
        bankRegistration?.deregister()
    }

    sealed class HomeAction {
        object BeginLoginFlow : HomeAction()
        object ClearMarkers : HomeAction()
        data class RemoveMarker(val marker: Marker) : HomeAction()
    }


}