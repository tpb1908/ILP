package com.tpb.coinz.bank

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.viewholder_divider.view.*

class DividerViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

    var text: String? = ""
        set(value) {
            field = value
            view.viewholder_divider_text.text = value
        }

}