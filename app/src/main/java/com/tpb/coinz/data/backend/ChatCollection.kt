package com.tpb.coinz.data.backend

import android.os.Parcelable
import com.tpb.coinz.Result
import kotlinx.android.parcel.Parcelize

interface ChatCollection {

    fun createThread(creator: UserCollection.User, partner: UserCollection.User, callback: (Result<Thread>) -> Unit)

    fun openThread(thread: Thread)

    fun closeThread(thread: Thread)

    fun postMessage(message: Message)

    fun getThreads(user: UserCollection.User, callback: (Result<List<Thread>>) -> Unit)

    @Parcelize
    data class Thread(val threadId: String, val creator: UserCollection.User, val partner: UserCollection.User): Parcelable

    data class Message(val message: String)
}