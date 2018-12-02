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

    private val collectedCoins = mutableListOf<SelectableItem<Coin>>()
    private val receivedCoins = mutableListOf<SelectableItem<Coin>>()

    private fun loadBankableCoins() {
        actions.postValue(BankAction.SetLoadingState(true))
        coinBank.getBankableCoins(userCollection.getCurrentUser()) {
            if (it is Result.Value) {
                collectedCoins.clear()
                collectedCoins.addAll(it.v.filterNot(Coin::received).map { SelectableItem(false, it) })
                receivedCoins.clear()
                receivedCoins.addAll(it.v.filter(Coin::received).map { SelectableItem(false, it) })
                bankableCoins.postValue(Pair(collectedCoins, receivedCoins))
            } else {
                //TODO error handling
            }
            actions.postValue(BankAction.SetLoadingState(false))
        }
        numStillBankable.postValue(coinBank.getNumBankable())

    }

    fun bankCoins() {
        actions.postValue(BankAction.SetLoadingState(true))
        val selected = (collectedCoins + receivedCoins).filter { it.selected }.map { it.item }
        coinBank.bankCoins(userCollection.getCurrentUser(), selected) { result ->
            if (result is Result.Value) {
                numStillBankable.postValue(coinBank.getNumBankable())
                collectedCoins.removeAll(result.v.map { SelectableItem(true, it) })
                receivedCoins.removeAll(result.v.map { SelectableItem(true, it) })
                bankableCoins.postValue(Pair(collectedCoins, receivedCoins))
            }
            actions.postValue(BankAction.SetLoadingState(false))
        }
    }

    override fun attemptSelect(item: SelectableItem<Coin>): Boolean {
        if (item.item.received) {
            item.selected = true
            return true
        }
        val bankable = coinBank.getNumBankable()
        if (numCollectedCoinsSelected < bankable) {
            item.selected = true
            numCollectedCoinsSelected++
            if (numCollectedCoinsSelected == bankable) selectionFull()
            return true
        }
        return false
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