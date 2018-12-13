package com.tpb.coinz.view.scoreboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tpb.coinz.R
import com.tpb.coinz.data.coin.scoreboard.Score

class ScoreboardAdapter : RecyclerView.Adapter<ScoreViewHolder>() {

    var scores: List<Score> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScoreViewHolder {
        return ScoreViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.viewholder_score, parent, false))
    }

    override fun getItemCount(): Int = scores.size

    override fun onBindViewHolder(holder: ScoreViewHolder, position: Int) {
        holder.score = scores[position]
    }
}