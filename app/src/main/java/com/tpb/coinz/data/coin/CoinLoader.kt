package com.tpb.coinz.data.coin

import java.util.*

interface CoinLoader {

    fun loadCoins(date: Calendar, listener: (Map?) -> Unit)

}