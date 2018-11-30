package com.tpb.coinz.view.bank

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tpb.coinz.R
import com.tpb.coinz.data.coin.Coin

class CoinRecyclerAdapter : RecyclerView.Adapter<SelectableViewHolder>() {

    private var userCoins: List<SelectableItem> = emptyList()
    private var sentCoins: List<SelectableItem> = emptyList()
    var isSelectionEnabled = true
    private var numStillBankable = 0
    private var numCollectedCoinsSelected = 0

    data class SelectableItem(var selected: Boolean, val coin: Coin)

    fun loadItems(newUserCoins: List<Coin>, newSentCoins: List<Coin>) {
        userCoins = newUserCoins.map { SelectableItem(false, it) }
        sentCoins = newSentCoins.map { SelectableItem(false, it) }
        //TODO: Diff and notify for exact changes
        notifyDataSetChanged()
    }

    fun getSelectedCoins(): List<Coin> =
            (userCoins + sentCoins).filter(SelectableItem::selected).map(SelectableItem::coin)

    fun setNumStillBankable(stillBankable: Int) {
        numStillBankable = stillBankable
    }

    var onClick: (Coin) -> Unit = {}

    private fun select(vh: CoinViewHolder, position: Int) {
        val item = getStateForPosition(position)
        if (position <= sentCoins.size) { // received coin
            if (item.selected) vh.deselect() else vh.select()
            item.selected = !item.selected
        } else if (!item.selected) {
            if (numCollectedCoinsSelected < numStillBankable) {
                vh.select()
                item.selected = true
                numCollectedCoinsSelected++
            } else {
                //TODO: Error should be handled in BankActivity
            }
        } else {
            vh.deselect()
            item.selected = false
            numCollectedCoinsSelected--
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectableViewHolder =
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

    private fun getStateForPosition(position: Int): SelectableItem = if (position > sentCoins.size) {
        userCoins[position - sentCoins.size - 2]
    } else {
        sentCoins[position - 1]
    }

    override fun onBindViewHolder(holder: SelectableViewHolder, position: Int) {
        // we also have holder.itemViewType, but auto-casts are nice
        if (holder is CoinViewHolder) {
            val coin = getStateForPosition(position).coin
            holder.itemView.setOnClickListener {
                if (isSelectionEnabled) {
                    select(holder, position)
                } else {
                    onClick(coin)
                }
            }
            holder.coin = coin
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