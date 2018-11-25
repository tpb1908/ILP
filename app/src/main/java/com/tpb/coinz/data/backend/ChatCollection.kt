package com.tpb.coinz.data.backend

import com.tpb.coinz.Result

interface ChatCollection {

    fun createThread(partnerId: String, callback: (Result<String>) -> Unit)

    fun openThread(partnerId: String)

    fun closeThread(partnerId: String)

    fun postMessage(message: String)

    fun getThreads()

}