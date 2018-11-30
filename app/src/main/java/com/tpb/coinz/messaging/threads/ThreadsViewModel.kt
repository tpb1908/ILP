package com.tpb.coinz.messaging.threads

import androidx.lifecycle.MediatorLiveData
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

    val threads = MediatorLiveData<List<ChatCollection.Thread>>()
    private val createdThreads = MutableLiveData<List<ChatCollection.Thread>>()

    private val receivedThreads = MutableLiveData<List<ChatCollection.Thread>>()

    init {
        var lastCreatedThreads = listOf<ChatCollection.Thread>()
        var lastReceivedThreads = listOf<ChatCollection.Thread>()
        threads.addSource(createdThreads) {
            lastCreatedThreads = it
            threads.postValue(lastCreatedThreads + lastReceivedThreads)
        }
        threads.addSource(receivedThreads) {
            lastReceivedThreads = it
            threads.postValue(lastCreatedThreads + lastReceivedThreads)
        }
    }

    val threadIntents = MutableLiveData<ChatCollection.Thread>()

    val userSearchResults = MutableLiveData<List<UserCollection.User>>()

    override val actions = MutableLiveData<ThreadsAction>()

    override fun bind() {
        actions.postValue(ThreadsAction.SetLoadingState(true))

        chatCollection.openThreads(userCollection.getCurrentUser()) {
            if (it is Result.Value<List<ChatCollection.Thread>>) {
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
            if (user is Result.Value<UserCollection.User>) {
                chatCollection.createThread(userCollection.getCurrentUser(), user.v) {
                    if (it is Result.Value<ChatCollection.Thread>) {
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


    sealed class ThreadsAction {
        class SetLoadingState(val loading: Boolean) : ThreadsAction()
    }
}