package com.tpb.coinz.data.coin.storage

import com.tpb.coinz.Result
import com.tpb.coinz.data.coin.Map
import java.util.*

interface MapStore {

    fun store(map: Map)

    fun update(map: Map)

    fun getLastStoreDate(callback: (Calendar) -> Unit)

    fun getLatest(callback: (Result<Map>) -> Unit)
}