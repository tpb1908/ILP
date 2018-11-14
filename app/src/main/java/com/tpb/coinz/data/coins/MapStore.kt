package com.tpb.coinz.data.coins

import java.util.*

interface MapStore {

    fun store(map: Map)

    fun getLastStoreDate(callback: (Calendar) -> Unit)

    fun getLatest(callback: (Map?) -> Unit)
}