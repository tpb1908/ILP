package com.tpb.coinz.data.coin.collection

import com.tpb.coinz.data.coin.Coin
import com.tpb.coinz.data.coin.Map
import com.tpb.coinz.data.users.User


/**
 * Interface for managing the collection of [Coin]s as the user moves and notifying relevant listeners
 */
interface CoinCollector {

    fun loadMap()

    fun addCollectionListener(listener: CoinCollectorListener)

    fun removeCollectionListener(listener: CoinCollectorListener)

    fun dispose()

    interface CoinCollectorListener {

        /**
         * Notify that [collected] [Coin]s have been collected
         */
        fun coinsCollected(collected: List<Coin>)

        /**
         * Notify that the currently valid [Map] has been loaded
         */
        fun mapLoaded(map: Map)

        /**
         * Notify that the [Map] is being reloaded due to being out of date
         */
        fun notifyReloading()

    }


}