package com.tpb.coinz.data.backend

import com.tpb.coinz.data.coin.Coin
import com.tpb.coinz.data.users.User

interface CoinCollection {

    fun collectCoin(user: User, coin: Coin)

    fun getCollectedCoins(user: User, listener: (List<Coin>) -> Unit)

    fun transferCoin(from: User, to: User, coin: Coin)

}