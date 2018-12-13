package com.tpb.coinz.view.bank

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tpb.coinz.R
import com.tpb.coinz.data.coin.Coin
import com.tpb.coinz.data.coin.Currency
import timber.log.Timber

class CoinRecyclerAdapter : RecyclerView.Adapter<SelectableViewHolder>() {

    private var collectedCoins: List<SelectableItem<Coin>> = emptyList()
    private var receivedCoins: List<SelectableItem<Coin>> = emptyList()
    private var rates: Map<Currency, Double>? = null
    var selectionManager: SelectionManager<Coin>? = null

    fun loadItems(newCollectedCoins: List<SelectableItem<Coin>>, newReceivedCoins: List<SelectableItem<Coin>>) {
        collectedCoins = newCollectedCoins
        receivedCoins = newReceivedCoins
        notifyDataSetChanged()
    }

    fun setRates(rates: Map<Currency, Double>) {
        this.rates = rates
    }

    fun numSelectedCoins() = collectedCoins.count(SelectableItem<Coin>::selected) + receivedCoins.count(SelectableItem<Coin>::selected)

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

    override fun getItemCount(): Int = collectedCoins.size + receivedCoins.size + 2

    override fun getItemViewType(position: Int): Int {
        // Headers at position 0 and position after sent coins
        return if (position == 0 || position == 1 + receivedCoins.size) 1 else 0
    }

    private fun getStateForPosition(position: Int): SelectableItem<Coin> = if (position > receivedCoins.size) {
        collectedCoins[position - receivedCoins.size - 2]
    } else {
        receivedCoins[position - 1]
    }

    override fun onBindViewHolder(holder: SelectableViewHolder, position: Int) {
        // we also have holder.itemViewType, but auto-casts are nice
        if (holder is CoinViewHolder) {
            val item = getStateForPosition(position)
            if (item.selected) holder.select() else holder.deselect()
            holder.itemView.setOnClickListener {
                select(holder, position)
            }
            holder.setText(item.item, rates?.get(item.item.currency))
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