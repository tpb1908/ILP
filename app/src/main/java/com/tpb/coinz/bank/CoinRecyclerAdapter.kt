package com.tpb.coinz.bank

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tpb.coinz.R
import com.tpb.coinz.data.coins.Coin

class CoinRecyclerAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var userCoins: List<Coin> = emptyList()
    private var sentCoins: List<Coin> = emptyList()

    fun loadItems(newUserCoins: List<Coin>, newSentCoins: List<Coin>) {
        userCoins = newUserCoins
        sentCoins = newSentCoins
        //TODO: Diff and notify for exact changes
        notifyDataSetChanged()
    }

    var onClick: (Coin) -> Unit =  {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
            if (viewType == 1)
                DividerViewHolder(LayoutInflater.from(parent.context).inflate(
                        R.layout.viewholder_divider, parent, false)
                )
            else
                CoinViewHolder(LayoutInflater.from(parent.context).inflate(
                        R.layout.viewholder_coin, parent, false)
                )

    override fun getItemCount(): Int = userCoins.size + sentCoins.size + 2

    override fun getItemViewType(position: Int): Int {
        // Headers at position 0 and position after sent coins
        return if (position == 0 || position == Math.max(1, sentCoins.size)) 1 else 0
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder.itemViewType == 0) {
            val coin = if (position > sentCoins.size) {
                userCoins[position - sentCoins.size - 2]
            } else {
                sentCoins[position - 1]
            }
            (holder as CoinViewHolder).coin = coin
            holder.itemView.setOnClickListener { onClick(coin) }
        } else {
            (holder as DividerViewHolder).text = holder.itemView.context.getString(
                    if (position == 0)
                        R.string.recycler_section_received_coins
                    else
                        R.string.recycler_section_collected_coins
            )
        }

    }
}