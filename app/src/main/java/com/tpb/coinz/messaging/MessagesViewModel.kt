package com.tpb.coinz.messaging

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.tpb.coinz.Result
import com.tpb.coinz.base.BaseViewModel
import com.tpb.coinz.data.backend.ChatCollection
import javax.inject.Inject

class MessagesViewModel(application: Application) : BaseViewModel<MessagesNavigator>(application) {

    @Inject lateinit var chatCollection: ChatCollection

    val threadIntents = MutableLiveData<String>()
    override fun bind() {

    }

    fun createChat(userId: String) {
        chatCollection.createThread(userId) {
            if (it is Result.Value<String>) {
                threadIntents.postValue(it.v)
            }
        }
    }

    fun openChat() {

    }
}