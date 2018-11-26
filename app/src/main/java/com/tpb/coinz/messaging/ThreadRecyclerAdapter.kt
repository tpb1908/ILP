package com.tpb.coinz.messaging

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tpb.coinz.R
import com.tpb.coinz.data.backend.ChatCollection

class ThreadRecyclerAdapter : RecyclerView.Adapter<MessageViewHolder>() {

    private val messages = mutableListOf<ChatCollection.Message>()

    fun setMessages(newMessages: List<ChatCollection.Message>) {
        messages.clear()
        messages.addAll(newMessages)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        return MessageViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.viewholder_message, parent, false))
    }

    override fun getItemCount(): Int = messages.size

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.message = messages[position]
        if (position%2 == 0) {
            holder.alignStart()
        } else {
            holder.alignEnd()
        }
    }
}