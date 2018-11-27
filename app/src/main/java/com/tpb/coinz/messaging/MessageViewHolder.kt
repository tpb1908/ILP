package com.tpb.coinz.messaging

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.tpb.coinz.R
import com.tpb.coinz.data.backend.ChatCollection
import kotlinx.android.synthetic.main.viewholder_message.view.*

class MessageViewHolder(val view: View): RecyclerView.ViewHolder(view) {
    var message: ChatCollection.Message? = null
        set(value) {
            field = value
            value?.let {
                view.viewholder_message_content.text = it.message
            }
        }

    fun alignStart() {
        view.viewholder_message_space_start.visibility = View.GONE
        view.viewholder_message_space_end.visibility = View.VISIBLE
        view.viewholder_message_bubble.setBackgroundResource(R.drawable.shape_message_bubble_start)
    }

    fun alignEnd() {
        view.viewholder_message_space_start.visibility = View.VISIBLE
        view.viewholder_message_space_end.visibility = View.GONE
        view.viewholder_message_bubble.setBackgroundResource(R.drawable.shape_message_bubble_end)
    }
}