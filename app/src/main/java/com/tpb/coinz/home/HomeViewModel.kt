package com.tpb.coinz.home

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.tpb.coinz.Result
import com.tpb.coinz.base.BaseViewModel
import com.tpb.coinz.data.backend.CoinCollection
import com.tpb.coinz.data.coins.CoinLoader
import com.tpb.coinz.data.coins.Map
import com.tpb.coinz.data.coins.MapStore
import dagger.Lazy
import java.util.*
import javax.inject.Inject

class HomeViewModel: BaseViewModel<HomeViewModel.HomeActions>(){

    @Inject
    lateinit var coinLoader: CoinLoader
    private var fbUser: FirebaseUser? = null

    @Inject
    lateinit var mapStore: MapStore

    @Inject lateinit var coinCollection: Lazy<CoinCollection>

    val user = MutableLiveData<FirebaseUser>()

    val collectionInfo = MutableLiveData<MapInfo>()

    override val actions: MutableLiveData<HomeActions> = MutableLiveData()

    override fun bind() {
        fbUser = FirebaseAuth.getInstance().currentUser
        Log.i(this::class.toString(), "User $fbUser")
        if (fbUser == null) {
            actions.postValue(HomeActions.BEGIN_LOGIN_FLOW)
        } else {
            user.postValue(fbUser)
            checkForCurrentMap()
        }

    }

    // Check whether we have a valid
    private fun checkForCurrentMap() {
        mapStore.getLatest {
            if (it is Result.Value<Map> && it.v.isValidForDay(Calendar.getInstance())) {
                postCoinCollectionInfo(it.v)
            } else {
                coinLoader.loadCoins(Calendar.getInstance()) {
                    map -> map?.apply { mapStore.store(this) }
                }
                postEmptyCollectionInfo()
            }
        }
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
                Log.i("HomeViewModel", "Stored user email ${it.email}")
            }
        }

        checkForCurrentMap()
    }

    fun userLoginFailed() {
        actions.postValue(HomeActions.BEGIN_LOGIN_FLOW)
    }

    enum class HomeActions {
        BEGIN_LOGIN_FLOW
    }

}