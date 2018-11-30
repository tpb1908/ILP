package com.tpb.coinz.data.coin.loading

import com.tpb.coinz.data.coin.Map
import java.util.*

interface MapLoader {

    fun loadCoins(date: Calendar, listener: (Map?) -> Unit)

}