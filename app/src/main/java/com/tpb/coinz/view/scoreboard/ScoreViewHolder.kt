package com.tpb.coinz.view.scoreboard

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.tpb.coinz.R
import com.tpb.coinz.data.coin.scoreboard.Score
import kotlinx.android.synthetic.main.viewholder_score.view.*

class ScoreViewHolder(val view: View): RecyclerView.ViewHolder(view) {
    var score: Score? = null
        set(value) {
            field = value
            value?.let {
                view.viewholder_score_text.text = view.context.getString(R.string.content_score, it.user.email, it.score)
            }
        }
}