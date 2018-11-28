package com.tpb.coinz.data.coins

import android.util.Log
import androidx.annotation.DrawableRes
import com.mapbox.mapboxsdk.geometry.LatLng
import com.tpb.coinz.R

data class Coin(val id: String,
                val value: Double,
                val currency: Currency,
                val markerSymbol: Int,
                val markerColor: Int,
                val location: LatLng,
                val banked: Boolean = false,
                val received: Boolean = false)

enum class Currency {
    PENY, DOLR, SHIL, QUID;

    companion object {
        fun fromString(name: String): Currency {
            return try {
                valueOf(name)
            } catch (e: IllegalArgumentException) {
                Log.e("Currency", "Invalid currency name $name")
                PENY
            }
        }
        @DrawableRes
        fun getImageId(currency: Currency): Int {
            return when (currency) {
                Currency.QUID -> R.drawable.ic_quid
                Currency.DOLR -> R.drawable.ic_dolr
                Currency.PENY -> R.drawable.ic_peny
                Currency.SHIL -> R.drawable.ic_shil
            }
        }
    }
}