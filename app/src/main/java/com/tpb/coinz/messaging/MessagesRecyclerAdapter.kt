package com.tpb.coinz.messaging

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tpb.coinz.R
import com.tpb.coinz.data.backend.ChatCollection

class MessagesRecyclerAdapter : RecyclerView.Adapter<MessagesViewHolder>() {

    private val threads = mutableListOf<ChatCollection.Thread>()

    fun setThreads(newThreads: List<ChatCollection.Thread>) {
        threads.clear()
        threads.addAll(newThreads)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessagesViewHolder {
        return MessagesViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.viewholder_thread, parent, false))
    }

    override fun getItemCount(): Int = threads.size

    override fun onBindViewHolder(holder: MessagesViewHolder, position: Int) {
        holder.thread = threads[position]
    }
}