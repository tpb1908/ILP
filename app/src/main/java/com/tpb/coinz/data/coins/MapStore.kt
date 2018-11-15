package com.tpb.coinz.data.coins

import com.tpb.coinz.Result
import java.util.*

interface MapStore {

    fun store(map: Map)

    fun update(map: Map)

    fun getLastStoreDate(callback: (Calendar) -> Unit)

    fun getLatest(callback: (Result<Map>) -> Unit)
}