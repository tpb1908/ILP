package com.tpb.coinz.messaging.thread

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tpb.coinz.R
import com.tpb.coinz.data.backend.ChatCollection

class MessageViewHolder(val view: View): RecyclerView.ViewHolder(view) {
    var message: ChatCollection.Message? = null
        set(value) {
            field = value
            value?.let {
                view.findViewById<TextView>(R.id.viewholder_message_content).text = it.message
            }
        }
}