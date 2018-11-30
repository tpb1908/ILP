package com.tpb.coinz.data.chat

import com.tpb.coinz.Result
import com.tpb.coinz.data.users.User

interface ChatCollection {

    fun createThread(creator: User, partner: User, callback: (Result<Thread>) -> Unit)

    fun openThread(thread: Thread, listener: (Result<List<Message>>) -> Unit)

    fun closeThread(thread: Thread)

    fun postMessage(message: Message, callback: (Result<Boolean>) -> Unit)

    fun openThreads(user: User, callback: (Result<List<Thread>>) -> Unit)

    fun closeThreads()

}