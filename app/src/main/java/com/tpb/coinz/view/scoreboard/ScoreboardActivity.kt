package com.tpb.coinz.view.scoreboard

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.tpb.coinz.R
import kotlinx.android.synthetic.main.activity_scoreboard.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class ScoreboardActivity : AppCompatActivity() {

    val vm: ScoreboardViewModel by viewModel()
    private val adapter = ScoreboardAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scoreboard)

        initViews()
        bindViewModel()
    }

    private fun initViews() {
        scoreboard_recycler.layoutManager = LinearLayoutManager(this)
        scoreboard_recycler.adapter = adapter
    }

    private fun bindViewModel() {
        vm.bind()
        vm.loadingState.observe(this, Observer {
            scoreboard_loading_bar.visibility = if (it) View.VISIBLE else View.GONE
        })
        vm.scores.observe(this, Observer {
            adapter.scores = it
        })
    }
}