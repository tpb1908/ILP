package com.tpb.coinz.messaging.thread

import androidx.lifecycle.MutableLiveData
import com.tpb.coinz.Result
import com.tpb.coinz.base.BaseViewModel
import com.tpb.coinz.data.backend.ChatCollection
import com.tpb.coinz.data.backend.CoinCollection
import com.tpb.coinz.data.backend.UserCollection
import com.tpb.coinz.data.coins.Coin
import timber.log.Timber
import javax.inject.Inject

class ThreadViewModel : BaseViewModel<ThreadViewModel.ThreadAction>() {

    @Inject lateinit var chatCollection: ChatCollection

    @Inject lateinit var userCollection: UserCollection

    @Inject lateinit var coinCollection: CoinCollection

    override val actions = MutableLiveData<ThreadAction>()

    private var thread: ChatCollection.Thread? = null

    val messages = MutableLiveData<List<ChatCollection.Message>>()

    val isCurrentUser: (UserCollection.User) -> Boolean = {
        userCollection.getCurrentUser() == it
    }

    override fun bind() {
    }

    fun openThread(thread: ChatCollection.Thread) {
        this.thread = thread
        chatCollection.openThread(thread, this::messageUpdate)
    }

    fun postMessage(message: String) {
        Timber.i("Posting message $message")
        chatCollection.postMessage(ChatCollection.Message(System.currentTimeMillis(), userCollection.getCurrentUser(), message)) {
            if (it is Result.Value) {

            }
        }
    }

    fun transferCoin(coin: Coin) {
        Timber.i("Transferring $coin in thread $thread")
        thread?.let {
            coinCollection.transferCoin(userCollection.getCurrentUser(), it.otherUser(userCollection.getCurrentUser()), coin)
        }
    }

    fun loadCoinsForTransfer() {
        actions.postValue(ThreadAction.SetLoadingState(true))
        coinCollection.getCollectedCoins(userCollection.getCurrentUser()) {
            actions.postValue(ThreadAction.SetLoadingState(false))
            actions.postValue(ThreadAction.ShowCoinsDialog(it))
        }
    }

    private fun messageUpdate(change: Result<List<ChatCollection.Message>>) {
        if (change is Result.Value) {
            actions.postValue(ThreadAction.SetLoadingState(true))
            messages.postValue((messages.value ?: emptyList()) + change.v)
            actions.postValue(ThreadAction.SetLoadingState(false))
        }
    }

    override fun onCleared() {
        super.onCleared()
        thread?.let { chatCollection.closeThread(it) }
    }

    sealed class ThreadAction {
        class SetLoadingState(val isLoading: Boolean) : ThreadAction()
        class ShowCoinsDialog(val coins: List<Coin>) : ThreadAction()
    }
}