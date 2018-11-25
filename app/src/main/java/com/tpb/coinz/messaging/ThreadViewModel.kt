package com.tpb.coinz.messaging

import androidx.lifecycle.MutableLiveData
import com.tpb.coinz.base.BaseViewModel
import com.tpb.coinz.data.backend.ChatCollection
import javax.inject.Inject

class ThreadViewModel : BaseViewModel<ThreadViewModel.ThreadAction>() {

    @Inject
    lateinit var chatCollection: ChatCollection

    override val actions = MutableLiveData<ThreadAction>()

    private var thread: ChatCollection.Thread? = null

    override fun bind() {

    }

    fun openThread(thread: ChatCollection.Thread) {
        this.thread = thread
    }

    override fun onCleared() {
        super.onCleared()
        //TODO: Close thread collection
    }

    sealed class ThreadAction {}
}