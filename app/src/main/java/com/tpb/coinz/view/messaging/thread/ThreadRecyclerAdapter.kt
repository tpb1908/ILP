package com.tpb.coinz.view.messaging.thread

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tpb.coinz.R
import com.tpb.coinz.data.chat.Message
import com.tpb.coinz.data.users.User

class ThreadRecyclerAdapter : RecyclerView.Adapter<MessageViewHolder>() {

    private val messages = mutableListOf<Message>()

    var isCurrentUser: (User) -> Boolean = { false }

    fun setMessages(newMessages: List<Message>) {
        messages.clear()
        messages.addAll(newMessages)
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return if (isCurrentUser(messages[position].sender)) R.layout.viewholder_message_sent else R.layout.viewholder_message_received
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        return MessageViewHolder(LayoutInflater.from(parent.context).inflate(viewType, parent, false))
    }

    override fun getItemCount(): Int = messages.size

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.setContent(messages[position], isCurrentUser(messages[position].sender))
    }
}