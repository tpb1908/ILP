package com.tpb.coinz.view.messaging.threads

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tpb.coinz.R
import com.tpb.coinz.data.chat.Thread

class ThreadsRecyclerAdapter : RecyclerView.Adapter<ThreadViewHolder>() {

    private val threads = mutableListOf<Thread>()

    fun setThreads(newThreads: List<Thread>) {
        threads.clear()
        threads.addAll(newThreads)
        notifyDataSetChanged()
    }

    var onClick: (Thread) -> Unit = {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ThreadViewHolder {
        return ThreadViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.viewholder_thread, parent, false))
    }

    override fun getItemCount(): Int = threads.size

    override fun onBindViewHolder(holder: ThreadViewHolder, position: Int) {
        holder.thread = threads[position]
        holder.itemView.setOnClickListener { onClick(threads[position]) }
    }
}