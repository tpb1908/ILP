package com.tpb.coinz.data.coin

import java.util.*


/**
 * Data class for a map within the Coinz game
 * @param dateGenerated The day that the map was generated
 * @param rates Mapping of currencies to their rates for the day that the map is valid
 * @param remainingCoins The coins which have not yet been collected
 * @param collectedCoins Coins which have been collected
 */
data class Map(val dateGenerated: Calendar,
               val rates: kotlin.collections.Map<Currency, Double>,
               val remainingCoins: MutableList<Coin>,
               val collectedCoins: MutableList<Coin> = mutableListOf()) {

    /**
     * Checks whether the provided calendar day is the same as that of the [Map]
     */
    fun isValidForDay(day: Calendar): Boolean {
        return day.get(Calendar.DAY_OF_YEAR) == dateGenerated.get(Calendar.DAY_OF_YEAR)
    }

}