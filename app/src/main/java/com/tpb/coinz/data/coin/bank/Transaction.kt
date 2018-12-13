package com.tpb.coinz.data.coin.bank

import com.tpb.coinz.data.coin.Coin

/**
 * Data class representing the transaction of banking a [Coin]
 * @param time The time at which the coin was banked
 * @param value The value of the coin in GOLD at the time of the transaction
 * @param coin The coin which was banked
 */
data class Transaction(val time: Long, val value: Double, val coin: Coin)