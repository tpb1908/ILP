package com.tpb.coinz.data.coin.loading

import com.tpb.coinz.data.coin.Map
import java.util.*

/**
 * Interface for loading the map for a given day
 */
interface MapLoader {

    /**
     *  Attempts to load the map for [date], returning [Result.Value] if the
     *  [Map] is successfully loaded
     */
    fun loadCoins(date: Calendar, listener: (Result<Map>) -> Unit)

}