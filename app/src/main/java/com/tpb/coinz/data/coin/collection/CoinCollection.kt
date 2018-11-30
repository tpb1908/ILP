package com.tpb.coinz.data.coin.collection

import com.tpb.coinz.data.coin.Coin
import com.tpb.coinz.data.users.User

interface CoinCollection {

    fun collectCoin(user: User, coin: Coin)

    fun getCollectedCoins(user: User, callback: (List<Coin>) -> Unit)

    fun transferCoin(from: User, to: User, coin: Coin)

}