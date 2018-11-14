package com.tpb.coinz.data.backend

import com.tpb.coinz.data.coins.Coin

interface CoinCollection {

    fun collectCoin(id: String, coin: Coin)

    fun getCollectedCoins(id: String)

}