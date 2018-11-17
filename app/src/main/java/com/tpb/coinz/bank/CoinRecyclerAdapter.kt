package com.tpb.coinz.bank

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tpb.coinz.R
import com.tpb.coinz.data.coins.Coin

class CoinRecyclerAdapter(): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var userCoins: List<Coin> = emptyList()
    var sentCoins: List<Coin> = emptyList()

    fun loadItems(newUserCoins: List<Coin>, newSentCoins: List<Coin>) {
        userCoins = newUserCoins
        sentCoins = newSentCoins
        //TODO: Diff and notify for exact changes
        notifyDataSetChanged()
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
            CoinViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.viewholder_coin, parent, false))

    override fun getItemCount(): Int = userCoins.size + sentCoins.size + 2

    override fun getItemViewType(position: Int): Int {
        // Headers at position 0 and position after sent coins
        return if (position == 0 || position == sentCoins.size) 1 else 0
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder.itemViewType == 0) {
            if (position > sentCoins.size + 1) {
                (holder as CoinViewHolder).coin = userCoins[position - sentCoins.size - 1]
            } else {
                (holder as CoinViewHolder).coin = sentCoins[position - 1]
            }
        } else {
            (holder as DividerViewHolder).text = "Some text"
        }

    }
}