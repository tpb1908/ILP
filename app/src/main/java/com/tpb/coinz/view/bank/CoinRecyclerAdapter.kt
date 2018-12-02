package com.tpb.coinz.view.bank

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tpb.coinz.R
import com.tpb.coinz.data.coin.Coin
import timber.log.Timber

class CoinRecyclerAdapter : RecyclerView.Adapter<SelectableViewHolder>() {

    private var userCoins: List<SelectableItem<Coin>> = emptyList()
    private var sentCoins: List<SelectableItem<Coin>> = emptyList()
    private var numStillBankable = 0
    var isSelectionEnabled = true
    var selectionManager: SelectionManager<Coin>? = null

    fun loadItems(newUserCoins: List<SelectableItem<Coin>>, newSentCoins: List<SelectableItem<Coin>>) {
        userCoins = newUserCoins
        sentCoins = newSentCoins
        //TODO: Diff and notify for exact changes
        notifyDataSetChanged()
    }

    fun numSelectedCoins() = userCoins.count(SelectableItem<Coin>::selected) + sentCoins.count(SelectableItem<Coin>::selected)

    fun getSelectedCoins(): List<Coin> =
            (userCoins + sentCoins).filter(SelectableItem<Coin>::selected).map(SelectableItem<Coin>::item)

    fun setNumStillBankable(stillBankable: Int) {
        numStillBankable = stillBankable
    }

    var onClick: (Coin) -> Unit = {}

    private fun select(vh: CoinViewHolder, position: Int) {
        val item = getStateForPosition(position)
        Timber.i("Selecting ViewHolder at position $position")
        if (item.selected) {
            vh.deselect()
            selectionManager?.deselect(item)
        } else {
            if (selectionManager?.attemptSelect(item) == true) {
                vh.select()
            }
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

    private fun getStateForPosition(position: Int): SelectableItem<Coin> = if (position > sentCoins.size) {
        userCoins[position - sentCoins.size - 2]
    } else {
        sentCoins[position - 1]
    }

    override fun onBindViewHolder(holder: SelectableViewHolder, position: Int) {
        // we also have holder.itemViewType, but auto-casts are nice
        if (holder is CoinViewHolder) {
            val item = getStateForPosition(position)
            if (item.selected) holder.select() else holder.deselect()
            holder.itemView.setOnClickListener {
                if (isSelectionEnabled) {
                    select(holder, position)
                } else {
                    onClick(item.item)
                }
            }
            holder.coin = item.item
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