package com.tpb.coinz.view.messaging.thread

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.tpb.coinz.R
import com.tpb.coinz.data.chat.Message
import com.tpb.coinz.data.coin.Currency
import kotlinx.android.synthetic.main.viewholder_message_sent.view.*

/**
 * ViewHolder for displaying a message in a thread
 * Displays either a text message, or a coin value and icon
 */
class MessageViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

    /**
     * If the [Message] has coin, sets the left or right hand compound drawable to the coin currency icon depending on
     * whether [isCurrentUser] is true or false, and the view text to the coin value
     * If the [Message] does not have a coin, the view text is set to [Message.message]
     */
    fun setContent(message: Message, isCurrentUser: Boolean) {
        if (message.coin == null) {
            view.viewholder_message_content.setCompoundDrawables(null, null, null, null)
            view.viewholder_message_content.text = message.message
        } else {
            val coin = message.coin
            if (isCurrentUser) {
                view.viewholder_message_content.setCompoundDrawablesWithIntrinsicBounds(Currency.getImageId(coin.currency), 0, 0, 0)
            } else {
                view.viewholder_message_content.setCompoundDrawablesWithIntrinsicBounds(0, 0, Currency.getImageId(coin.currency), 0)
            }
            view.viewholder_message_content.text = view.resources.getString(R.string.text_coin_message, coin.currency, coin.value)
        }
    }

}