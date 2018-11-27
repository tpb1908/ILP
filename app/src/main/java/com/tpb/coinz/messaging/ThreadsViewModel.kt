package com.tpb.coinz.messaging

import androidx.lifecycle.MutableLiveData
import com.tpb.coinz.Result
import com.tpb.coinz.base.BaseViewModel
import com.tpb.coinz.data.backend.ChatCollection
import com.tpb.coinz.data.backend.UserCollection
import timber.log.Timber
import javax.inject.Inject

class ThreadsViewModel : BaseViewModel<ThreadsViewModel.ThreadsAction>() {

    @Inject lateinit var chatCollection: ChatCollection

    @Inject lateinit var userCollection: UserCollection

    val threads = MutableLiveData<List<ChatCollection.Thread>>()

    val threadIntents = MutableLiveData<ChatCollection.Thread>()

    val userSearchResults = MutableLiveData<List<UserCollection.User>>()

    override val actions = MutableLiveData<ThreadsAction>()

    override fun bind() {
        actions.postValue(ThreadsAction.SetLoadingState(true))
        chatCollection.openThreads(userCollection.getCurrentUser()) {
            if (it is Result.Value<List<ChatCollection.Thread>>) {
                Timber.i("Retrieved threads ${it.v}")
                threads.postValue(it.v + (threads.value ?: emptyList()))
                actions.postValue(ThreadsAction.SetLoadingState(false))
            }
        }
    }

    fun createChat(userEmail: String) {
        actions.postValue(ThreadsAction.SetLoadingState(true))
        userCollection.retrieveUserFromEmail(userEmail) { user ->
            if (user is Result.Value<UserCollection.User>) {
                chatCollection.createThread(userCollection.getCurrentUser(), user.v) {
                    if (it is Result.Value<ChatCollection.Thread>) {
                        threads.postValue((threads.value?: emptyList()) + it.v)
                        actions.postValue(ThreadsAction.SetLoadingState(false))
                        threadIntents.postValue(it.v)
                    }
                }
            }
        }
    }

    fun openChat() {

    }

    fun searchUsers(partialEmail: String) {
        userCollection.searchUsersByEmail(partialEmail) {
            if (it is Result.Value) userSearchResults.postValue(it.v)
        }
    }


    sealed class ThreadsAction {
        class SetLoadingState(val loading: Boolean): ThreadsAction()
    }
}