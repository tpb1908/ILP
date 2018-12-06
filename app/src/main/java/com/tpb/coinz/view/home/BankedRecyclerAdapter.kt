package com.tpb.coinz.view.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tpb.coinz.R
import com.tpb.coinz.data.coin.Coin
import com.tpb.coinz.data.coin.Currency
import kotlinx.android.synthetic.main.viewholder_coin_simple.view.*

class BankedRecyclerAdapter : RecyclerView.Adapter<BankedRecyclerAdapter.SimpleCoinViewHolder>() {

    var coins: List<Coin> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimpleCoinViewHolder =
        SimpleCoinViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.viewholder_coin_simple, parent, false))


    override fun getItemCount(): Int = coins.size

    override fun onBindViewHolder(holder: SimpleCoinViewHolder, position: Int) {
        holder.coin = coins[position]
    }

    class SimpleCoinViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        var coin: Coin? = null
            set(value) {
                field = value
                value?.let {
                    view.viewholder_coin_icon.setImageResource(Currency.getImageId(it.currency))
                    view.viewholder_coin_value.text = it.value.toString()
                }
            }
    }

}