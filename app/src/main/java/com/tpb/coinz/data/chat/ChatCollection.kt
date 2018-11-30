package com.tpb.coinz.data.chat

import android.os.Parcelable
import com.tpb.coinz.Result
import com.tpb.coinz.data.users.User
import kotlinx.android.parcel.Parcelize

interface ChatCollection {

    fun createThread(creator: User, partner: User, callback: (Result<Thread>) -> Unit)

    fun openThread(thread: Thread, listener: (Result<List<Message>>) -> Unit)

    fun closeThread(thread: Thread)

    fun postMessage(message: Message, callback: (Result<Boolean>) -> Unit)

    fun openThreads(user: User, callback: (Result<List<Thread>>) -> Unit)

    fun closeThreads()

    @Parcelize
    data class Thread(val threadId: String, val creator: User, val partner: User) : Parcelable {

        fun otherUser(user: User): User = if (user == creator) partner else creator

    }

    data class Message(val timestamp: Long, val sender: User, val message: String)
}