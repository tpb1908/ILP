package com.tpb.coinz.data.backend

interface MessageCollection {

    fun openThread(partnerId: String)

    fun closeThread(partnerId: String)

    fun postMessage(message: String)

}