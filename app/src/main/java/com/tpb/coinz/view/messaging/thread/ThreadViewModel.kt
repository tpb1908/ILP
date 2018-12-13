package com.tpb.coinz.view.messaging.thread

import androidx.annotation.StringRes
import androidx.lifecycle.MutableLiveData
import com.tpb.coinz.R
import com.tpb.coinz.data.chat.ChatCollection
import com.tpb.coinz.data.chat.Message
import com.tpb.coinz.data.chat.Thread
import com.tpb.coinz.data.coin.Coin
import com.tpb.coinz.data.coin.bank.CoinBank
import com.tpb.coinz.data.coin.collection.CoinCollection
import com.tpb.coinz.data.users.User
import com.tpb.coinz.data.users.UserCollection
import com.tpb.coinz.data.util.Registration
import com.tpb.coinz.view.base.ActionLiveData
import com.tpb.coinz.view.base.BaseViewModel
import timber.log.Timber


class ThreadViewModel(private val chatCollection: ChatCollection,
                      private val userCollection: UserCollection,
                      private val coinCollection: CoinCollection,
                      private val coinBank: CoinBank) : BaseViewModel<ThreadViewModel.ThreadAction>() {


    override val actions = ActionLiveData<ThreadAction>()

    private var thread: Thread? = null

    val messages = MutableLiveData<List<Message>>()

    val isCurrentUser: (User) -> Boolean = {
        userCollection.getCurrentUser() == it
    }

    private var chatRegistration: Registration? = null

    override fun bind() {
        // Nothing to do here as thread is passed via Intent to ThreadActivity
    }

    fun openThread(thread: Thread) {
        this.thread = thread
        if (chatRegistration == null ) {
            chatRegistration = chatCollection.openThread(thread, this::messageUpdate)
        }
    }

    fun postTextMessage(message: String) {
        Timber.i("Posting message $message")
        postMessage(Message(System.currentTimeMillis(), userCollection.getCurrentUser(), message))
    }

    private fun postMessage(message: Message) {
        chatCollection.postMessage(message) {
            if (it.isFailure) {
                actions.postValue(ThreadAction.DisplayError(R.string.error_posting_message) {postMessage(message)})
            } else {
                actions.postValue(ThreadAction.ClearMessageEntry)
            }
        }
    }

    fun transferCoin(coin: Coin) {
        Timber.i("Transferring $coin in thread $thread")
        thread?.let {
            coinCollection.transferCoin(
                    userCollection.getCurrentUser(),
                    it.otherUser(userCollection.getCurrentUser()),
                    coin) { result ->
                result.onSuccess { message ->
                    postMessage(message)
                }.onFailure {
                    actions.postValue(ThreadAction.DisplayError(R.string.error_sending_coin) {transferCoin(coin)})
                }
            }
        }
    }

    fun loadCoinsForTransfer() {
        val numStillBankable = coinBank.getNumBankable()
        if (numStillBankable == 0 || true) {
            loadingState.postValue(true)
            coinCollection.getCollectedCoins(userCollection.getCurrentUser()) { result ->
                loadingState.postValue(false)
                result.onSuccess {
                    loadingState.postValue(false)
                    actions.postValue(ThreadAction.ShowCoinsDialog(it))
                }.onFailure {
                    actions.postValue(ThreadAction.DisplayError(R.string.error_loading_coins) {loadCoinsForTransfer()})
                }
            }
        } else {
            actions.postValue(ThreadAction.DisplayBankDialog(numStillBankable))
        }
    }

    private fun messageUpdate(change: Result<List<Message>>) {
        change.onSuccess {
            loadingState.postValue(true)
            messages.postValue((messages.value ?: emptyList()) + it)
            loadingState.postValue(false)
        }
    }

    override fun onCleared() {
        super.onCleared()
        chatRegistration?.deregister()
    }

    sealed class ThreadAction {
        data class ShowCoinsDialog(val coins: List<Coin>) : ThreadAction()
        data class DisplayBankDialog(val numStillBankable: Int) : ThreadAction()
        data class DisplayError(@StringRes val message: Int, val retry: () -> Unit): ThreadAction()
        object ClearMessageEntry : ThreadAction()
    }
}