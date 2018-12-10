package com.tpb.coinz.view.messaging.threads

import android.text.format.DateUtils
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.tpb.coinz.R
import com.tpb.coinz.data.chat.Thread
import kotlinx.android.synthetic.main.viewholder_thread.view.*

/**
 * ViewHolder for a [Thread]
 * Displays the [Thread] participants and the last time that the [Thread] was updated
 */
class ThreadViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
    var thread: Thread? = null
        set(value) {
            field = value
            value?.let {
                view.viewholder_thread_title.text = view.context.getString(R.string.text_thread_users,
                        it.creator.email,
                        it.partner.email)
                view.viewholder_thread_info.text = view.context.getString(R.string.text_thread_last_update,
                        DateUtils.getRelativeTimeSpanString(it.updated))
            }
        }
}