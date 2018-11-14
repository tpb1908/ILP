package com.tpb.coinz.data.coins

import java.util.*

interface CoinLoader {

    fun loadCoins(date: Calendar, listener: (Map?) -> Unit)

}