package com.tpb.coinz.data.backend

interface ChatCollection {

    fun createThread(partnerId: String)

    fun openThread(partnerId: String)

    fun closeThread(partnerId: String)

    fun postMessage(message: String)

    fun getThreads()

}