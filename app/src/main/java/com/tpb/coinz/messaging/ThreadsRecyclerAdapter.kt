package com.tpb.coinz.messaging

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tpb.coinz.R
import com.tpb.coinz.data.backend.ChatCollection

class ThreadsRecyclerAdapter : RecyclerView.Adapter<ThreadViewHolder>() {

    private val threads = mutableListOf<ChatCollection.Thread>()

    fun setThreads(newThreads: List<ChatCollection.Thread>) {
        threads.clear()
        threads.addAll(newThreads)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ThreadViewHolder {
        return ThreadViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.viewholder_thread, parent, false))
    }

    override fun getItemCount(): Int = threads.size

    override fun onBindViewHolder(holder: ThreadViewHolder, position: Int) {
        holder.thread = threads[position]
    }
}