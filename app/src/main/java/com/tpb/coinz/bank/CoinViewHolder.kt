package com.tpb.coinz.bank

import android.view.View
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.recyclerview.widget.RecyclerView
import com.tpb.coinz.R
import com.tpb.coinz.data.coins.Coin
import com.tpb.coinz.data.coins.Currency
import kotlinx.android.synthetic.main.viewholder_coin.view.*

class CoinViewHolder(val view: View): RecyclerView.ViewHolder(view) {
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

}