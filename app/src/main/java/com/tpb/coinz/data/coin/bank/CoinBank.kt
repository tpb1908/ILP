package com.tpb.coinz.data.coin.bank

import com.tpb.coinz.data.coin.Coin
import com.tpb.coinz.data.users.User

interface CoinBank {

    fun bankCoins(user: User, coins: List<Coin>, callback: Result<Boolean>) {

    }

    fun getBankableCoins(user: User, callback: (com.tpb.coinz.Result<List<Coin>>) -> Unit)

    fun getNumBankable()

}