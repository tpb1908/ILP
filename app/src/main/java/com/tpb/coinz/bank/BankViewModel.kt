package com.tpb.coinz.bank

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.tpb.coinz.App
import com.tpb.coinz.base.BaseViewModel
import com.tpb.coinz.data.backend.CoinCollection
import com.tpb.coinz.data.coins.Coin
import javax.inject.Inject

class BankViewModel(application: Application) : BaseViewModel<BankNavigator>(application) {

    @Inject
    lateinit var coinCollection: CoinCollection

    val availableCoins = MutableLiveData<Pair<List<Coin>, List<Coin>>>()

    private var user: FirebaseUser? = null

    override fun bind() {
        user = FirebaseAuth.getInstance().currentUser
        loadCollectedCoins()
    }

    private fun loadCollectedCoins() {
        user?.let {
            coinCollection.getCollectedCoins(it.uid) {
                availableCoins.postValue(Pair(it, emptyList()))
            }
        }
    }

}