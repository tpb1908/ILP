package com.tpb.coinz.view.bank

import androidx.lifecycle.MutableLiveData
import com.tpb.coinz.Result
import com.tpb.coinz.data.coin.Coin
import com.tpb.coinz.data.coin.bank.CoinBank
import com.tpb.coinz.data.users.UserCollection
import com.tpb.coinz.view.base.BaseViewModel
import javax.inject.Inject

class BankViewModel : BaseViewModel<BankViewModel.BankAction>(), CoinRecyclerAdapter.SelectionListener {

    @Inject lateinit var coinBank: CoinBank

    @Inject lateinit var userCollection: UserCollection

    val bankableCoins = MutableLiveData<Pair<List<Coin>, List<Coin>>>()

    val numStillBankable = MutableLiveData<Int>()


    override val actions = MutableLiveData<BankAction>()

    override fun bind() {
        loadBankableCoins()
    }

    private fun loadBankableCoins() {
        actions.postValue(BankAction.SetLoadingState(true))
        coinBank.getBankableCoins(userCollection.getCurrentUser()) {
            if (it is Result.Value) {
                bankableCoins.postValue(it.v.partition(Coin::received))
            } else {
                //TODO error handling
            }
            actions.postValue(BankAction.SetLoadingState(false))
        }
        numStillBankable.postValue(coinBank.getNumBankable())

    }

    override fun selected(coin: Coin) {

    }

    override fun deselected(coin: Coin) {
    }

    override fun selectionFull() {
    }

    sealed class BankAction {
        data class SetLoadingState(val loading: Boolean) : BankAction()
    }

}