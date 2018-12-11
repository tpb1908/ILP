package com.tpb.coinz.view.bank

import android.view.View
import com.tpb.coinz.R
import com.tpb.coinz.data.coin.Coin
import kotlinx.android.synthetic.main.viewholder_coin.view.*

class CoinViewHolder(val view: View) : SelectableViewHolder(view) {
    fun setText(coin: Coin, rate: Double?) {
        view.viewholder_coin_icon.setImageResource(coin.currency.img)
        if (rate == null) {
            view.viewholder_coin_value.text = String.format("%.2f", coin.value)
        } else {
            view.viewholder_coin_value.text = String.format("%.2f | %.2f", coin.value, coin.value * rate)
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