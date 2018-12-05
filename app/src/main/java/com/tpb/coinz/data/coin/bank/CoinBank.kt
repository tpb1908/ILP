package com.tpb.coinz.data.coin.bank

import com.tpb.coinz.data.coin.Coin
import com.tpb.coinz.data.users.User
import com.tpb.coinz.data.util.Registration

interface CoinBank {

    fun bankCoins(user: User, coins: List<Coin>, callback: (Result<List<Coin>>) -> Unit)

    fun getBankableCoins(user: User, listener: (Result<List<Coin>>) -> Unit): Registration

    fun getBankedCoins(user: User, listener: (Result<List<Coin>>) -> Unit): Registration

    fun getRecentlyBankedCoins(user: User, count: Int, listener: (Result<List<Coin>>) -> Unit): Registration

    fun getNumBankable(): Int

}