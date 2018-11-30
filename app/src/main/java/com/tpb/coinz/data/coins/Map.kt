package com.tpb.coinz.data.coins

import java.util.*
import kotlin.collections.Map

data class Map(val dateGenerated: Calendar, val rates: Map<Currency, Double>, val remainingCoins: MutableList<Coin>, val collectedCoins: MutableList<Coin> = mutableListOf()) {

    fun isValidForDay(day: Calendar): Boolean {
        return day.get(Calendar.DAY_OF_YEAR) == dateGenerated.get(Calendar.DAY_OF_YEAR)
    }

}