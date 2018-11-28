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
                view.viewholder_coin_icon.setImageResource(Currency.getImageId(it.currency))
                view.viewholder_coin_value.text = it.value.toString()
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