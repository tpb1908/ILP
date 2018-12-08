package com.tpb.coinz.view.messaging.threads

import androidx.annotation.StringRes
import androidx.lifecycle.MutableLiveData
import com.tpb.coinz.R
import com.tpb.coinz.data.chat.ChatCollection
import com.tpb.coinz.data.chat.Thread
import com.tpb.coinz.data.users.User
import com.tpb.coinz.data.users.UserCollection
import com.tpb.coinz.data.util.Registration
import com.tpb.coinz.view.base.ActionLiveData
import com.tpb.coinz.view.base.BaseViewModel
import timber.log.Timber


class ThreadsViewModel(private val chatCollection: ChatCollection,
                       private val userCollection: UserCollection) : BaseViewModel<ThreadsViewModel.ThreadsAction>() {


    val threads = MutableLiveData<List<Thread>>()
    private val allThreads = mutableListOf<Thread>()

    val threadIntents = MutableLiveData<Thread>()

    val userSearchResults = MutableLiveData<List<User>>()

    override val actions = ActionLiveData<ThreadsAction>()

    private var threadsRegistration: Registration? = null

    override fun bind() {
        if (threadsRegistration == null) {
            loadThreads()
        }
    }

    private fun loadThreads() {
        actions.postValue(ThreadsAction.SetLoadingState(true))
        threadsRegistration = chatCollection.openThreads(userCollection.getCurrentUser()) { result ->
            result.onSuccess {
                Timber.i("Retrieved threads $it")
                allThreads.addAll(it)
                threads.postValue(allThreads)
                actions.postValue(ThreadsAction.SetLoadingState(false))
            }.onFailure {
                actions.postValue(ThreadsAction.SetLoadingState(false))
                actions.postValue(ThreadsAction.DisplayError(R.string.error_loading_threads, this::loadThreads))
            }
        }
    }

    fun createChat(userEmail: String) {
        actions.postValue(ThreadsAction.SetLoadingState(true))
        if (userCollection.getCurrentUser().email == userEmail) {
            actions.postValue(ThreadsAction.SetLoadingState(false))
            actions.postValue(ThreadsAction.DisplayMessage(R.string.error_thread_to_self))
        } else {
            userCollection.retrieveUserFromEmail(userEmail) { result ->
                result.onSuccess { user ->
                    createThread(user)
                }.onFailure {
                    Timber.e("Couldn't retrieve user to create thread")
                    actions.postValue(ThreadsAction.SetLoadingState(false))
                    actions.postValue(ThreadsAction.DisplayError(R.string.error_loading_user) {createChat(userEmail)})
                }
            }
        }
    }

    private fun createThread(user: User) {
        chatCollection.createThread(userCollection.getCurrentUser(), user) { threadResult ->
            threadResult.onSuccess {
                Timber.i("Creating thread")
                threads.postValue((threads.value ?: emptyList()) + it)
                threadIntents.postValue(it)
                actions.postValue(ThreadsAction.SetLoadingState(false))

            }.onFailure {
                actions.postValue(ThreadsAction.SetLoadingState(false))
                actions.postValue(ThreadsAction.DisplayError(R.string.error_creating_thread) {createThread(user)})
            }
        }
    }


    fun searchUsers(partialEmail: String) {
        userCollection.searchUsersByEmail(partialEmail) { result ->
            result.onSuccess { userSearchResults.postValue(it) }
            // We don't care about failure, that just means no match
        }
    }


    override fun onCleared() {
        super.onCleared()
        threadsRegistration?.deregister()
    }

    sealed class ThreadsAction {
        data class SetLoadingState(val loading: Boolean) : ThreadsAction()
        data class DisplayMessage(@StringRes val message: Int) : ThreadsAction()
        data class DisplayError(@StringRes val message: Int, val retry: () -> Unit): ThreadsAction()
    }
}