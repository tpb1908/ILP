package com.tpb.coinz.data.coin.collection

import com.tpb.coinz.data.coin.Coin
import com.tpb.coinz.data.users.User

/**
 * Interface for managing the [Coins][Coin] collected by a user
 */
interface CoinCollection {

    /**
     * Registers the [coin] as being collected by the [user]
     * TODO: Callback
     */
    fun collectCoin(user: User, coin: Coin)

    /**
     * Loads all coins collected by [user]
     */
    fun getCollectedCoins(user: User, callback: (List<Coin>) -> Unit)

    /**
     * Transfer [coin] from [User] [from] to [User] [to]
     * TODO: Callback
     */
    fun transferCoin(from: User, to: User, coin: Coin)

}