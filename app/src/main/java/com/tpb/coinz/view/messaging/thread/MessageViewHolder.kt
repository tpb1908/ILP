package com.tpb.coinz.view.messaging.thread

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.tpb.coinz.R
import com.tpb.coinz.data.chat.Message
import com.tpb.coinz.data.coin.Currency
import com.tpb.coinz.orElse
import kotlinx.android.synthetic.main.viewholder_message_sent.view.*

class MessageViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
    var message: Message? = null
        set(value) {
            field = value
            value?.let {
                view.viewholder_message_content.text = it.message
            }
        }

    fun setMessageType(isCurrentUser: Boolean) {
        message?.coin?.let { coin ->
            if (isCurrentUser) {
                view.viewholder_message_content.setCompoundDrawablesWithIntrinsicBounds(Currency.getImageId(coin.currency), 0, 0, 0)
            } else {
                view.viewholder_message_content.setCompoundDrawablesWithIntrinsicBounds(0, 0, Currency.getImageId(coin.currency), 0)
            }
            view.viewholder_message_content.text = view.resources.getString(R.string.text_coin_message, coin.currency, coin.value)
        }.orElse {
            view.viewholder_message_content.setCompoundDrawables(null, null, null, null)
        }
    }
}