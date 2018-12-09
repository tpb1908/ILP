package com.tpb.coinz.view.bank

import androidx.annotation.StringRes
import androidx.lifecycle.MutableLiveData
import com.tpb.coinz.R
import com.tpb.coinz.data.coin.Coin
import com.tpb.coinz.data.coin.bank.CoinBank
import com.tpb.coinz.data.coin.scoreboard.Scoreboard
import com.tpb.coinz.data.coin.storage.MapStore
import com.tpb.coinz.data.users.UserCollection
import com.tpb.coinz.data.util.CompositeRegistration
import com.tpb.coinz.view.base.ActionLiveData
import com.tpb.coinz.view.base.BaseViewModel


class BankViewModel(private val coinBank: CoinBank,
                    private val userCollection: UserCollection,
                    private val mapStore: MapStore,
                    private val scoreboard: Scoreboard) :
        BaseViewModel<BankViewModel.BankAction>(), SelectionManager<Coin> {

    val bankableCoins = MutableLiveData<Pair<List<SelectableItem<Coin>>, List<SelectableItem<Coin>>>>()

    val numStillBankable = MutableLiveData<Int>()

    val numCollectedSelected = MutableLiveData<Int>()

    override val actions = ActionLiveData<BankAction>()

    private var numCollectedCoinsSelected = 0
        set(value) {
            field = value
            numCollectedSelected.postValue(value)
        }

    private val registrations = CompositeRegistration()

    override fun bind() {
        if (firstBind) loadBankableCoins()
        super.bind()
    }

    private val collectedCoins = mutableListOf<SelectableItem<Coin>>()
    private val receivedCoins = mutableListOf<SelectableItem<Coin>>()

    private fun loadBankableCoins() {
        actions.postValue(BankAction.SetLoadingState(true))
        registrations.add(coinBank.getBankableCoins(userCollection.getCurrentUser()) { result ->
            result.onSuccess { coins ->
                collectedCoins.clear()
                collectedCoins.addAll(coins.filterNot(Coin::received).map { SelectableItem(false, it) })
                receivedCoins.clear()
                receivedCoins.addAll(coins.filter(Coin::received).map { SelectableItem(false, it) })
                bankableCoins.postValue(Pair(collectedCoins, receivedCoins))
            }.onFailure {
                actions.postValue(BankAction.DisplayError(R.string.error_loading_coins, this::loadBankableCoins))
            }

            actions.postValue(BankAction.SetLoadingState(false))
        })
        numStillBankable.postValue(coinBank.getNumBankable())

    }

    fun bankCoins() {
        actions.postValue(BankAction.SetLoadingState(true))
        val selected = (collectedCoins + receivedCoins).filter { it.selected }.map { it.item }
        mapStore.getLatest { result ->
            result.onSuccess { map ->
                coinBank.bankCoins(userCollection.getCurrentUser(), selected, map.rates) { result ->
                    result.onSuccess { coins ->
                        numStillBankable.postValue(coinBank.getNumBankable())
                        collectedCoins.removeAll(coins.map { SelectableItem(true, it) })
                        receivedCoins.removeAll(coins.map { SelectableItem(true, it) })
                        bankableCoins.postValue(Pair(collectedCoins, receivedCoins))
                        increaseScore(coins)
                    }.onFailure {
                        actions.postValue(BankAction.DisplayError(R.string.error_banking_coins, this::bankCoins))
                    }
                    actions.postValue(BankAction.SetLoadingState(false))
                }
            }.onFailure {
                actions.postValue(BankAction.DisplayError(R.string.error_banking_coins, this::bankCoins))
                actions.postValue(BankAction.SetLoadingState(false))
            }

        }

    }

    private fun increaseScore(bankedCoins: List<Coin>) {
        mapStore.getLatest {
            it.onSuccess {  map ->
                val increase = bankedCoins.sumByDouble { coin ->
                    coin.value * map.rates.getValue(coin.currency)
                }
                scoreboard.increaseScore(userCollection.getCurrentUser(), increase) {

                }
            }
        }
    }

    override fun attemptSelect(item: SelectableItem<Coin>): Boolean {
        if (item.selected) return false
        if (item.item.received) {
            item.selected = true
            return true
        }
        val bankable = coinBank.getNumBankable()
        if (numCollectedCoinsSelected < bankable) {
            item.selected = true
            numCollectedCoinsSelected++
            if (numCollectedCoinsSelected == bankable) actions.postValue(BankAction.SelectionFull)
            return true
        }
        return false
    }

    override fun deselect(item: SelectableItem<Coin>) {
        if (item.selected) {
            item.selected = false
            if (!item.item.received) numCollectedCoinsSelected--
        }
    }

    override fun onCleared() {
        super.onCleared()
        registrations.deregister()
    }

    sealed class BankAction {
        data class SetLoadingState(val loading: Boolean) : BankAction()
        data class DisplayError(@StringRes val message: Int, val retry: () -> Unit): BankAction()
        object SelectionFull : BankAction()
    }

}