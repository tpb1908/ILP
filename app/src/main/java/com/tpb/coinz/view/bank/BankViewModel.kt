package com.tpb.coinz.view.bank

import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.tpb.coinz.view.base.BaseViewModel
import com.tpb.coinz.data.backend.CoinCollection
import com.tpb.coinz.data.coin.Coin
import javax.inject.Inject

class BankViewModel : BaseViewModel<BankViewModel.BankAction>() {

    @Inject
    lateinit var coinCollection: CoinCollection

    val availableCoins = MutableLiveData<Pair<List<Coin>, List<Coin>>>()

    private var user: FirebaseUser? = null

    override val actions = MutableLiveData<BankAction>()

    override fun bind() {
        user = FirebaseAuth.getInstance().currentUser
        loadCollectedCoins()
    }

    private fun loadCollectedCoins() {
//        user?.let {
//            coinCollection.getCollectedCoins(it.uid) {
//                availableCoins.postValue(Pair(it, emptyList()))
//            }
//        }
    }

    sealed class BankAction

}