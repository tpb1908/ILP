package com.tpb.coinz.messaging

import androidx.lifecycle.MutableLiveData
import com.tpb.coinz.Result
import com.tpb.coinz.base.BaseViewModel
import com.tpb.coinz.data.backend.ChatCollection
import com.tpb.coinz.data.backend.UserCollection
import timber.log.Timber
import javax.inject.Inject

class ThreadViewModel : BaseViewModel<ThreadViewModel.ThreadAction>() {

    @Inject
    lateinit var chatCollection: ChatCollection

    @Inject lateinit var userCollection: UserCollection

    override val actions = MutableLiveData<ThreadAction>()

    private var thread: ChatCollection.Thread? = null

    val messages = MutableLiveData<List<ChatCollection.Message>>()

    val isCurrentUser: (UserCollection.User) -> Boolean = {
        userCollection.getCurrentUser() == it }

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

    private fun messageUpdate(change: Result<List<ChatCollection.Message>>) {
        if (change is Result.Value) {
            actions.postValue(ThreadAction.DISPLAY_LOADING)
            messages.postValue((messages.value ?: emptyList()) + change.v)
            actions.postValue(ThreadAction.HIDE_LOADING)
        }
    }

    override fun onCleared() {
        super.onCleared()
        thread?.let { chatCollection.closeThread(it) }
    }

    enum class ThreadAction {
        DISPLAY_LOADING, HIDE_LOADING
    }
}