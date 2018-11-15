package com.tpb.coinz.home

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestoreException
import com.tpb.coinz.App
import com.tpb.coinz.Result
import com.tpb.coinz.base.BaseViewModel
import com.tpb.coinz.data.backend.CoinCollection
import com.tpb.coinz.data.coins.CoinLoader
import com.tpb.coinz.data.coins.Map
import com.tpb.coinz.data.coins.MapStore
import dagger.Lazy
import java.util.*
import javax.inject.Inject

class HomeViewModel(application: Application) : BaseViewModel<HomeNavigator>(application) {

    @Inject
    lateinit var coinLoader: CoinLoader
    private var fbUser: FirebaseUser? = null

    @Inject
    lateinit var mapStore: MapStore

    @Inject lateinit var coinCollection: Lazy<CoinCollection>

    val user = MutableLiveData<FirebaseUser>()

    override fun init() {
        (getApplication() as App).homeComponent.inject(this)
        fbUser = FirebaseAuth.getInstance().currentUser
        Log.i(this::class.toString(), "User $fbUser")
        if (fbUser == null) {
            navigator.get()?.beginLoginFlow()
        } else {
            user.postValue(fbUser)
        }
        mapStore.getLatest {
            if (it is Result.Value<Map> && it.v.isValidForDay(Calendar.getInstance())) {
                postCoinCollectionInfo()
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

    }

    private fun postCoinCollectionInfo() {
        fbUser?.let {
          coinCollection.get().getCollectedCoins(it.uid, object: EventListener<DocumentSnapshot> {
              override fun onEvent(p0: DocumentSnapshot?, p1: FirebaseFirestoreException?) {

              }
          })
        }
    }

    fun userLoggedIn() {
        fbUser = FirebaseAuth.getInstance().currentUser
        user.postValue(fbUser)
        postCoinCollectionInfo()
    }

    fun userLoginFailed() {
        navigator.get()?.beginLoginFlow()
    }



}