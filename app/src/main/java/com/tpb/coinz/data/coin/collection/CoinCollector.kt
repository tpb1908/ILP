package com.tpb.coinz.data.coin.collection

import com.tpb.coinz.data.coin.Coin
import com.tpb.coinz.data.coin.Map
import com.tpb.coinz.data.users.User

interface CoinCollector {

    fun setCoinCollection(coinCollection: CoinCollection, user: User)

    fun loadMap()

    fun addCollectionListener(listener: CoinCollectorListener)

    fun removeCollectionListener(listener: CoinCollectorListener)

    fun dispose()

    interface CoinCollectorListener {

        fun coinsCollected(collected: List<Coin>)

        fun mapLoaded(map: Map)

        fun notifyReloading()

    }


}