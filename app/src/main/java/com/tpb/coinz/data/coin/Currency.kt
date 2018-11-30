package com.tpb.coinz.data.coin

import androidx.annotation.DrawableRes
import com.tpb.coinz.R
import timber.log.Timber

enum class Currency {
    PENY, DOLR, SHIL, QUID;

    companion object {
        fun fromString(name: String): Currency {
            return try {
                valueOf(name)
            } catch (e: IllegalArgumentException) {
                Timber.e("Invalid currency name $name")
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