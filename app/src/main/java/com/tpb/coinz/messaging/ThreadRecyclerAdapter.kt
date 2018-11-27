package com.tpb.coinz.messaging

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tpb.coinz.R
import com.tpb.coinz.data.backend.ChatCollection
import com.tpb.coinz.data.backend.UserCollection

class ThreadRecyclerAdapter : RecyclerView.Adapter<MessageViewHolder>() {

    private val messages = mutableListOf<ChatCollection.Message>()

    var isCurrentUser: (UserCollection.User) -> Boolean = {false}

    fun setMessages(newMessages: List<ChatCollection.Message>) {
        messages.clear()
        messages.addAll(newMessages)
        notifyDataSetChanged()
    }

    fun addMessage(message: ChatCollection.Message) {
        messages.add(message)
        notifyItemInserted(messages.size-1)
    }

    fun addMessages(newMessages: List<ChatCollection.Message>) {
        val currentLength = messages.size
        messages.addAll(newMessages)
        notifyItemRangeInserted(currentLength, messages.size-1)
    }

    override fun getItemViewType(position: Int): Int {
        return if(isCurrentUser(messages[position].sender)) R.layout.viewholder_message_sent else R.layout.viewholder_message_received
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        return MessageViewHolder(LayoutInflater.from(parent.context).inflate(viewType, parent, false))
    }

    override fun getItemCount(): Int = messages.size

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.message = messages[position]

    }
}