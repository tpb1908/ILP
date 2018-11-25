package com.tpb.coinz.data.backend

import com.firebase.ui.auth.data.model.User
import com.tpb.coinz.Result

interface ChatCollection {

    fun createThread(creator: UserCollection.User, partner: UserCollection.User, callback: (Result<Thread>) -> Unit)

    fun openThread(creator: User, partner: User)

    fun closeThread(partnerId: String)

    fun postMessage(message: String)

    fun getThreads(user: UserCollection.User, callback: (Result<List<Thread>>) -> Unit)

    data class Thread(val threadId: String, val partner: UserCollection.User)
}