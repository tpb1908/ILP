package com.tpb.coinz.view.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tpb.coinz.R
import com.tpb.coinz.data.coin.bank.Transaction
import kotlinx.android.synthetic.main.viewholder_coin_simple.view.*

class BankedRecyclerAdapter : RecyclerView.Adapter<BankedRecyclerAdapter.SimpleCoinViewHolder>() {

    var transactions: List<Transaction> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimpleCoinViewHolder =
        SimpleCoinViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.viewholder_coin_simple, parent, false))


    override fun getItemCount(): Int = transactions.size

    override fun onBindViewHolder(holder: SimpleCoinViewHolder, position: Int) {
        holder.transaction = transactions[position]
    }

    class SimpleCoinViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        var transaction: Transaction? = null
            set(value) {
                field = value
                value?.let {
                    view.viewholder_coin_icon.setImageResource(it.coin.currency.img)
                    view.viewholder_coin_value.text = it.value.toString()
                }
            }
    }

}