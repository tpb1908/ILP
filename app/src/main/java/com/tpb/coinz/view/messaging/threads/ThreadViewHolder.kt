package com.tpb.coinz.view.messaging.threads

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.tpb.coinz.data.chat.Thread
import kotlinx.android.synthetic.main.viewholder_thread.view.*

class ThreadViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
    var thread: Thread? = null
        set(value) {
            field = value
            value?.let {
                view.viewholder_thread_title.text = it.partner.email
                view.viewholder_thread_info.text = it.threadId
            }
        }


}