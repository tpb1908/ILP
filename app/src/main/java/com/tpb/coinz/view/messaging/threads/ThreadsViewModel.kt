package com.tpb.coinz.view.messaging.threads

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.tpb.coinz.Registration
import com.tpb.coinz.Result
import com.tpb.coinz.view.base.BaseViewModel
import com.tpb.coinz.data.chat.ChatCollection
import com.tpb.coinz.data.chat.Thread
import com.tpb.coinz.data.users.User
import com.tpb.coinz.data.users.UserCollection
import timber.log.Timber
import javax.inject.Inject

class ThreadsViewModel : BaseViewModel<ThreadsViewModel.ThreadsAction>() {

    @Inject lateinit var chatCollection: ChatCollection

    @Inject lateinit var userCollection: UserCollection

    val threads = MediatorLiveData<List<Thread>>()
    private val createdThreads = MutableLiveData<List<Thread>>()

    private val receivedThreads = MutableLiveData<List<Thread>>()

    val threadIntents = MutableLiveData<Thread>()

    val userSearchResults = MutableLiveData<List<User>>()

    override val actions = MutableLiveData<ThreadsAction>()

    private var threadsRegistration: Registration? = null

    init {
        var lastCreatedThreads = listOf<Thread>()
        var lastReceivedThreads = listOf<Thread>()
        threads.addSource(createdThreads) {
            lastCreatedThreads = it
            threads.postValue(lastCreatedThreads + lastReceivedThreads)
        }
        threads.addSource(receivedThreads) {
            lastReceivedThreads = it
            threads.postValue(lastCreatedThreads + lastReceivedThreads)
        }
    }

    override fun bind() {
        actions.postValue(ThreadsAction.SetLoadingState(true))

        chatCollection.openThreads(userCollection.getCurrentUser()) {
            if (it is Result.Value<List<Thread>>) {
                if (it.v.isNotEmpty()) {
                    Timber.i("Retrieved threads ${it.v}")
                    if (it.v.first().creator == userCollection.getCurrentUser()) {
                        createdThreads.postValue(it.v)
                    } else {
                        receivedThreads.postValue(it.v)
                    }
                }
                actions.postValue(ThreadsAction.SetLoadingState(false))
            }
        }
    }

    fun createChat(userEmail: String) {
        actions.postValue(ThreadsAction.SetLoadingState(true))
        userCollection.retrieveUserFromEmail(userEmail) { user ->
            if (user is Result.Value<User>) {
                chatCollection.createThread(userCollection.getCurrentUser(), user.v) {
                    if (it is Result.Value<Thread>) {
                        threads.postValue((threads.value ?: emptyList()) + it.v)
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


    override fun onCleared() {
        super.onCleared()
        threadsRegistration?.deregister()
    }

    sealed class ThreadsAction {
        class SetLoadingState(val loading: Boolean) : ThreadsAction()
    }
}