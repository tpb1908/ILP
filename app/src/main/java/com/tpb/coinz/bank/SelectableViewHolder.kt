package com.tpb.coinz.bank

import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class SelectableViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    abstract fun select()

    abstract fun deselect()


}