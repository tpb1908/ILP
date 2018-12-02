package com.tpb.coinz.view.bank

import androidx.lifecycle.MutableLiveData
import com.tpb.coinz.Result
import com.tpb.coinz.data.coin.Coin
import com.tpb.coinz.data.coin.bank.CoinBank
import com.tpb.coinz.data.users.UserCollection
import com.tpb.coinz.view.base.BaseViewModel
import javax.inject.Inject

class BankViewModel : BaseViewModel<BankViewModel.BankAction>(), SelectionManager<Coin> {

    @Inject lateinit var coinBank: CoinBank

    @Inject lateinit var userCollection: UserCollection

    val bankableCoins = MutableLiveData<Pair<List<SelectableItem<Coin>>, List<SelectableItem<Coin>>>>()

    val numStillBankable = MutableLiveData<Int>()

    override val actions = MutableLiveData<BankAction>()

    private var numCollectedCoinsSelected = 0


    override fun bind() {
        if (firstBind) loadBankableCoins()
        super.bind()
    }

    private fun loadBankableCoins() {
        actions.postValue(BankAction.SetLoadingState(true))
        coinBank.getBankableCoins(userCollection.getCurrentUser()) {
            if (it is Result.Value) {
                bankableCoins.postValue(it.v.map { SelectableItem(false, it) }.partition { it.item.received })
            } else {
                //TODO error handling
            }
            actions.postValue(BankAction.SetLoadingState(false))
        }
        numStillBankable.postValue(coinBank.getNumBankable())

    }

    fun bankCoins() {

    }

    override fun attemptSelect(item: SelectableItem<Coin>): Boolean {
        if (!item.item.received) {
            return if (numCollectedCoinsSelected < coinBank.getNumBankable()) {
                numCollectedCoinsSelected++
                item.selected = true
                true
            } else {
                false
            }
        }
        item.selected = true
        return true
    }

    override fun deselect(item: SelectableItem<Coin>) {
        item.selected = false
        if (!item.item.received) numCollectedCoinsSelected--
    }


    fun selectionFull() {
        actions.postValue(BankAction.SelectionFull)
    }

    sealed class BankAction {
        data class SetLoadingState(val loading: Boolean) : BankAction()
        object SelectionFull : BankAction()
    }

}