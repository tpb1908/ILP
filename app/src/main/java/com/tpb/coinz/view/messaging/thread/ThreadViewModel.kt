package com.tpb.coinz.view.messaging.thread

import androidx.lifecycle.MutableLiveData
import com.tpb.coinz.data.util.Registration
import com.tpb.coinz.Result
import com.tpb.coinz.view.base.BaseViewModel
import com.tpb.coinz.data.chat.ChatCollection
import com.tpb.coinz.data.chat.Message
import com.tpb.coinz.data.chat.Thread
import com.tpb.coinz.data.coin.collection.CoinCollection
import com.tpb.coinz.data.users.UserCollection
import com.tpb.coinz.data.coin.Coin
import com.tpb.coinz.data.users.User
import com.tpb.coinz.view.base.ActionLiveData
import timber.log.Timber
import javax.inject.Inject

class ThreadViewModel : BaseViewModel<ThreadViewModel.ThreadAction>() {

    @Inject lateinit var chatCollection: ChatCollection

    @Inject lateinit var userCollection: UserCollection

    @Inject lateinit var coinCollection: CoinCollection

    override val actions = ActionLiveData<ThreadAction>()

    private var thread: Thread? = null

    val messages = MutableLiveData<List<Message>>()

    val isCurrentUser: (User) -> Boolean = {
        userCollection.getCurrentUser() == it
    }

    private var chatRegistration: Registration? = null

    override fun bind() {
    }

    fun openThread(thread: Thread) {
        this.thread = thread
        chatRegistration = chatCollection.openThread(thread, this::messageUpdate)
    }

    fun postMessage(message: String) {
        Timber.i("Posting message $message")
        chatCollection.postMessage(Message(System.currentTimeMillis(), userCollection.getCurrentUser(), message)) {
            if (it is Result.Value) {

            }
        }
    }

    fun transferCoin(coin: Coin) {
        Timber.i("Transferring $coin in thread $thread")
        thread?.let {
            coinCollection.transferCoin(userCollection.getCurrentUser(), it.otherUser(userCollection.getCurrentUser()), coin) {
                if (it is Result.Value) {
                    chatCollection.postMessage(it.v, {})
                }
            }
        }
    }

    fun loadCoinsForTransfer() {
        actions.postValue(ThreadAction.SetLoadingState(true))
        coinCollection.getCollectedCoins(userCollection.getCurrentUser()) {
            actions.postValue(ThreadAction.SetLoadingState(false))
            if (it is Result.Value) {
                actions.postValue(ThreadAction.ShowCoinsDialog(it.v))
            } else {
                //TODO: Error display
            }
        }
    }

    private fun messageUpdate(change: Result<List<Message>>) {
        if (change is Result.Value) {
            actions.postValue(ThreadAction.SetLoadingState(true))
            messages.postValue((messages.value ?: emptyList()) + change.v)
            actions.postValue(ThreadAction.SetLoadingState(false))
        }
    }

    override fun onCleared() {
        super.onCleared()
        chatRegistration?.deregister()
    }

    sealed class ThreadAction {
        class SetLoadingState(val isLoading: Boolean) : ThreadAction()
        class ShowCoinsDialog(val coins: List<Coin>) : ThreadAction()
    }
}