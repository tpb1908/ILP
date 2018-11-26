package com.tpb.coinz.messaging

import androidx.lifecycle.MutableLiveData
import com.tpb.coinz.Result
import com.tpb.coinz.base.BaseViewModel
import com.tpb.coinz.data.backend.ChatCollection
import javax.inject.Inject

class ThreadViewModel : BaseViewModel<ThreadViewModel.ThreadAction>() {

    @Inject
    lateinit var chatCollection: ChatCollection

    override val actions = MutableLiveData<ThreadAction>()

    private var thread: ChatCollection.Thread? = null

    val messages = MutableLiveData<List<ChatCollection.Message>>()

    override fun bind() {
        messages.postValue(listOf(
                ChatCollection.Message("text1"), ChatCollection.Message("text2"), ChatCollection.Message("text3\nline2\nline3")
        ))
    }

    fun openThread(thread: ChatCollection.Thread) {
        this.thread = thread
        chatCollection.openThread(thread, this::messageUpdate)
    }

    fun postMessage(message: String) {
        chatCollection.postMessage(ChatCollection.Message(message), {

        })
    }

    private fun messageUpdate(change: Result<List<ChatCollection.Message>>) {
        if (change is Result.Value) {
            messages.postValue(change.v)
        }
    }

    override fun onCleared() {
        super.onCleared()
        //TODO: Close thread collection
    }

    sealed class ThreadAction {}
}