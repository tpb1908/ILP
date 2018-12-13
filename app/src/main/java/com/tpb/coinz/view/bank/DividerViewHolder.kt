package com.tpb.coinz.view.bank

import android.view.View
import kotlinx.android.synthetic.main.viewholder_divider.view.*

class DividerViewHolder(val view: View) : SelectableViewHolder(view) {

    var text: String? = ""
        set(value) {
            field = value
            view.viewholder_divider_text.text = value
        }

    override fun select() {}

    override fun deselect() {
    }
}