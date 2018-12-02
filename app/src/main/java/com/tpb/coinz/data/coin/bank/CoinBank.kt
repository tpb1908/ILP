package com.tpb.coinz.data.coin.bank

import com.tpb.coinz.Registration
import com.tpb.coinz.Result
import com.tpb.coinz.data.coin.Coin
import com.tpb.coinz.data.users.User

interface CoinBank {

    fun bankCoins(user: User, coins: List<Coin>, callback: (Result<List<Coin>>) -> Unit)

    fun getBankableCoins(user: User, listener: (Result<List<Coin>>) -> Unit): Registration

    fun getNumBankable(): Int

}