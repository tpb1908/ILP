package com.tpb.coinz.bank

import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class SelectableViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    public abstract fun select()

    public abstract fun deselect()


}