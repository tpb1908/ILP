package com.tpb.coinz.data.backend

import com.tpb.coinz.data.coins.Coin

interface CoinCollection {

    fun collectCoin(user: UserCollection.User, coin: Coin)

    fun getCollectedCoins(user: UserCollection.User, listener: (List<Coin>) -> Unit)

    fun transferCoin(from: UserCollection.User, to: UserCollection.User, coin: Coin)

}