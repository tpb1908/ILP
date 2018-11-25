package com.tpb.coinz.messaging

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.tpb.coinz.Result
import com.tpb.coinz.base.BaseViewModel
import com.tpb.coinz.data.backend.ChatCollection
import com.tpb.coinz.data.backend.UserCollection
import javax.inject.Inject

class MessagesViewModel(application: Application) : BaseViewModel<MessagesNavigator>(application) {

    @Inject lateinit var chatCollection: ChatCollection

    @Inject lateinit var userCollection: UserCollection

    val threadIntents = MutableLiveData<String>()
    override fun bind() {

    }

    fun createChat(userEmail: String) {
        userCollection.retrieveUserFromEmail(userEmail) { user ->
            if (user is Result.Value<UserCollection.User>) {
                chatCollection.createThread(userCollection.getCurrentUser() ,user.v) {
                    if (it is Result.Value<String>) {
                        threadIntents.postValue(it.v)
                    }
                }
            }
        }
    }

    fun openChat() {

    }
}