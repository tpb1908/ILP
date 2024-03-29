package com.tpb.coinz.data.coin.collection

import com.tpb.coinz.data.chat.Message
import com.tpb.coinz.data.coin.Coin
import com.tpb.coinz.data.users.User

/**
 * Interface for managing the [Coins][Coin] collected by a user
 */
interface CoinCollection {


    fun collectCoins(user: User, coins: List<Coin>, callback: (Result<List<Coin>>) -> Unit)

    /**
     * Loads all coins collected by [user]
     */
    fun getCollectedCoins(user: User, callback: (Result<List<Coin>>) -> Unit)


    /**
     * Transfer [coin] from [User] [from] to [User] [to]
     */
    fun transferCoin(from: User, to: User, coin: Coin, callback: (Result<Message>) -> Unit)

}