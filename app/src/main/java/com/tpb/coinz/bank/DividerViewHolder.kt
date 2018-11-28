@file:Suppress("SpellCheckingInspection", "SpellCheckingInspection")

package com.tpb.coinz.bank

import android.view.View
import kotlinx.android.synthetic.main.viewholder_divider.view.*

@Suppress("SpellCheckingInspection", "SpellCheckingInspection", "SpellCheckingInspection")
class DividerViewHolder(val view: View) : SelectableViewHolder(view) {

    var text: String? = ""
        set(value) {
            field = value
            view.viewholder_divider_text.text = value
        }

    override fun select() {
        //TODO: Some form of selection when all viewholders in section are selected
    }

    override fun deselect() {
    }
}