package com.tpb.coinz.bank

import android.view.View
import androidx.annotation.DrawableRes
import com.tpb.coinz.R
import com.tpb.coinz.data.coins.Coin
import com.tpb.coinz.data.coins.Currency
import kotlinx.android.synthetic.main.viewholder_coin.view.*

class CoinViewHolder(val view: View): SelectableViewHolder(view) {
    var coin: Coin? = null
        set(value) {
            field = value
            value?.let {
                view.viewholder_coin_icon.setImageResource(getImageId(it.currency))
                view.viewholder_coin_value.text = it.value.toString()
            }
        }

    @DrawableRes
    private fun getImageId(currency: Currency): Int {
        return when (currency) {
            Currency.QUID -> R.drawable.ic_quid
            Currency.DOLR -> R.drawable.ic_dolr
            Currency.PENY -> R.drawable.ic_peny
            Currency.SHIL -> R.drawable.ic_shil
        }
    }

    private val originalBackground = view.viewholder_coin_card.cardBackgroundColor

    override fun select() {
        view.viewholder_coin_card.setCardBackgroundColor(view.context.resources.getColor(R.color.colorAccent))
    }

    override fun deselect() {
        view.viewholder_coin_card.setCardBackgroundColor(originalBackground)
    }
}