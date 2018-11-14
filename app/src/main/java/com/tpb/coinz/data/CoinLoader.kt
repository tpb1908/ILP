package com.tpb.coinz.data

import java.util.*

interface CoinLoader {

    fun loadCoins(date: Calendar, listener: (Map?) -> Unit)

}