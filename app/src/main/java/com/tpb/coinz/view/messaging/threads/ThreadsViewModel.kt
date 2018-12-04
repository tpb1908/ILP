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
import javax.inject.Inject

class ThreadsViewModel : BaseViewModel<ThreadsViewModel.ThreadsAction>() {

    @Inject lateinit var chatCollection: ChatCollection

    @Inject lateinit var userCollection: UserCollection

    val threads = MutableLiveData<List<Thread>>()
    private val allThreads = mutableListOf<Thread>()

    val threadIntents = MutableLiveData<Thread>()

    val userSearchResults = MutableLiveData<List<User>>()

    override val actions = ActionLiveData<ThreadsAction>()

    private var threadsRegistration: Registration? = null

    override fun bind() {
        actions.postValue(ThreadsAction.SetLoadingState(true))

        threadsRegistration = chatCollection.openThreads(userCollection.getCurrentUser()) {result ->
            result.onSuccess {
                Timber.i("Retrieved threads ${it}")
                allThreads.addAll(it)
                threads.postValue(allThreads)
                actions.postValue(ThreadsAction.SetLoadingState(false))
            }.onFailure {

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
                    chatCollection.createThread(userCollection.getCurrentUser(), user) { threadResult ->
                        threadResult.onSuccess {
                            Timber.i("Creating thread")
                            threads.postValue((threads.value ?: emptyList()) + it)
                            threadIntents.postValue(it)
                        }.onFailure {

                        }

                        actions.postValue(ThreadsAction.SetLoadingState(false))
                    }
                }.onFailure {
                    Timber.e("Couldn't retrieve user to create thread")
                    actions.postValue(ThreadsAction.SetLoadingState(false))
                }
            }
        }
    }


    fun searchUsers(partialEmail: String) {
        userCollection.searchUsersByEmail(partialEmail) { result ->
            result.onSuccess { userSearchResults.postValue(it) }
        }
    }


    override fun onCleared() {
        super.onCleared()
        threadsRegistration?.deregister()
    }

    sealed class ThreadsAction {
        class SetLoadingState(val loading: Boolean) : ThreadsAction()
        class DisplayMessage(@StringRes val message: Int) : ThreadsAction()
    }
}