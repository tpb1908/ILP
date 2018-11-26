package com.tpb.coinz.messaging

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.tpb.coinz.data.backend.ChatCollection
import kotlinx.android.synthetic.main.viewholder_thread.view.*

class ThreadViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
    var thread: ChatCollection.Thread? = null
        set(value) {
            field = value
            value?.let {
                view.viewholder_thread_title.text = it.partner.email
                view.viewholder_thread_info.text = it.threadId
            }
        }


}