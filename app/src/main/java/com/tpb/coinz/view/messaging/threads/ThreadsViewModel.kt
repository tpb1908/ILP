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
        if (threadsRegistration == null) { // Check that we aren't already receiving updates
            loadThreads()
        }
    }

    private fun loadThreads() {
        loadingState.postValue(true)
        // Register to receive live updates to threads which the user is part of
        threadsRegistration = chatCollection.openThreads(userCollection.getCurrentUser()) { result ->
            result.onSuccess {
                Timber.i("Retrieved threads $it")
                allThreads.addAll(it)
                threads.postValue(allThreads)
                loadingState.postValue(false)
            }.onFailure {
                loadingState.postValue(false)
                actions.postValue(ThreadsAction.DisplayError(R.string.error_loading_threads, this::loadThreads))
            }
        }
    }

    fun createChat(userEmail: String) {
        loadingState.postValue(true)
        if (userCollection.getCurrentUser().email == userEmail) {
            loadingState.postValue(false)
            actions.postValue(ThreadsAction.DisplayMessage(R.string.error_thread_to_self, emptyArray()))
        } else {
            // Check that the thread doesn't already exist
            val existing = allThreads.filter { it.partner.email == userEmail }
            if (existing.isEmpty()) {
                // Check that the user actually exists and attempt to create the thread
                userCollection.retrieveUserFromEmail(userEmail) { result ->
                    result.onSuccess { user ->
                        createThread(user)
                    }.onFailure {
                        Timber.e("Couldn't retrieve user to create thread")
                        loadingState.postValue(false)
                        actions.postValue(ThreadsAction.DisplayError(R.string.error_loading_user) {createChat(userEmail)})
                    }
                }
            } else {
                actions.postValue(ThreadsAction.DisplayMessage(R.string.error_thread_already_exists, arrayOf(userEmail)))
                loadingState.postValue(false)
            }

        }
    }

    private fun createThread(user: User) {
        chatCollection.createThread(userCollection.getCurrentUser(), user) { threadResult ->
            threadResult.onSuccess {
                Timber.i("Created thread $it")
                // New thread will appear in the list via the threadsRegistration
                loadingState.postValue(false)
                threadIntents.postValue(it) // Open the new thread
            }.onFailure {
                loadingState.postValue(false)
                actions.postValue(ThreadsAction.DisplayError(R.string.error_creating_thread) {createThread(user)})
            }
        }
    }

    /**
     * Search for a [User] by a partial email and return any matching
     * users via [userSearchResults]
     */
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
        data class DisplayMessage(@StringRes val message: Int, val formatArgs: Array<Any>) : ThreadsAction()
        data class DisplayError(@StringRes val message: Int, val retry: () -> Unit): ThreadsAction()
    }
}