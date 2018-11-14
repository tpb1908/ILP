package com.tpb.coinz.data.coins

import androidx.room.Embedded
import java.util.*
import kotlin.collections.Map

data class Map(val dateGenerated: Date, val rates: Map<Currency, Double>, val coins: List<Coin>)