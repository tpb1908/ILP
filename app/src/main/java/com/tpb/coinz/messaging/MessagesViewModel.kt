package com.tpb.coinz.messaging

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.tpb.coinz.Result
import com.tpb.coinz.base.BaseViewModel
import com.tpb.coinz.data.backend.ChatCollection
import com.tpb.coinz.data.backend.UserCollection
import javax.inject.Inject

class MessagesViewModel(application: Application) : BaseViewModel<MessagesNavigator>(application) {

    @Inject lateinit var chatCollection: ChatCollection

    @Inject lateinit var userCollection: UserCollection

    val threads = MutableLiveData<List<ChatCollection.Thread>>()

    val threadIntents = MutableLiveData<ChatCollection.Thread>()
    override fun bind() {
        chatCollection.getThreads(userCollection.getCurrentUser()) {
            if (it is Result.Value<List<ChatCollection.Thread>>) {
                Log.i("MessagesViewModel", "Retrieved threads ${it.v}")
                threads.postValue(it.v)
            }
        }
    }

    fun createChat(userEmail: String) {
        userCollection.retrieveUserFromEmail(userEmail) { user ->
            if (user is Result.Value<UserCollection.User>) {
                chatCollection.createThread(userCollection.getCurrentUser(), user.v) {
                    if (it is Result.Value<ChatCollection.Thread>) {
                        threads.postValue(threads.value?.plus(it.v))
                        threadIntents.postValue(it.v)
                    }
                }
            }
        }
    }

    fun openChat() {

    }
}