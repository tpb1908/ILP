package com.tpb.coinz.data.coin.storage

import com.tpb.coinz.data.coin.Map

/**
 * Interface for storing [Maps][Map]
 */
interface MapStore {

    /**
     * Store a [Map] instance for retrieval across app restarts
     */
    fun store(map: Map)

    /**
     * Update a [Map] instance in the store
     */
    fun update(map: Map)

    /**
     * Return the most recent [Map] if it exists
     */
    fun getLatest(callback: (Result<Map>) -> Unit)
}