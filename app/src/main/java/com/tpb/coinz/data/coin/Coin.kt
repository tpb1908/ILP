package com.tpb.coinz.data.coin

import com.mapbox.mapboxsdk.geometry.LatLng

/**
 * Data class for a coin within the Coinz game
 * @param id Unique id for the [Coin]
 * @param value Value of the coin in its [Currency]
 * @param currency The coin currency, PENY, DOLR, SHIL, or QUID
 * @param markerSymbol The value to be displayed on the coins marker
 * @param markerColor The colour of the coins marker on the map
 * @param location The location of the coin
 * @param received Whether the coin was received from another user as spare change
 */
data class Coin(val id: String,
                val value: Double,
                val currency: Currency,
                val markerSymbol: Int,
                val markerColor: Int,
                val location: LatLng,
                val received: Boolean = false)

