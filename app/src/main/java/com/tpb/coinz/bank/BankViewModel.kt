package com.tpb.coinz.bank

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.tpb.coinz.App
import com.tpb.coinz.base.BaseViewModel
import com.tpb.coinz.data.backend.CoinCollection
import com.tpb.coinz.data.coins.Coin
import javax.inject.Inject

class BankViewModel(application: Application) : BaseViewModel<BankNavigator>(application) {

    @Inject
    lateinit var coinCollection: CoinCollection

    val availableCoins = MutableLiveData<Pair<List<Coin>, List<Coin>>>()

    override fun init() {
        (getApplication() as App).bankComponent.inject(this)
    }
}