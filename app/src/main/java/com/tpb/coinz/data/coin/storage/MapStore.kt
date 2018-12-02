package com.tpb.coinz.data.coin.storage

import com.tpb.coinz.Result
import com.tpb.coinz.data.coin.Map

/**
 * Interface for storing [Maps][Map]
 */
interface MapStore {

    fun store(map: Map)

    fun update(map: Map)

    fun getLatest(callback: (Result<Map>) -> Unit)
}