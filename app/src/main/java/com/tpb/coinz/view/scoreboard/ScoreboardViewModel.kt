package com.tpb.coinz.view.scoreboard

import androidx.annotation.StringRes
import androidx.lifecycle.MutableLiveData
import com.tpb.coinz.R
import com.tpb.coinz.data.coin.scoreboard.Score
import com.tpb.coinz.data.coin.scoreboard.Scoreboard
import com.tpb.coinz.data.util.Registration
import com.tpb.coinz.view.base.ActionLiveData
import com.tpb.coinz.view.base.BaseViewModel

class ScoreboardViewModel(private val scoreboard: Scoreboard) : BaseViewModel<ScoreboardViewModel.ScoreboardAction>() {

    override val actions = ActionLiveData<ScoreboardAction>()

    val scores = MutableLiveData<List<Score>>()
    private var registration: Registration? = null

    override fun bind() {
        super.bind()
        if (registration == null) {
            loadScores()
        }
    }

    private fun loadScores() {
        loadingState.postValue(true)
        registration = scoreboard.getScores { result ->
            loadingState.postValue(false)
            result.onSuccess {
                scores.postValue(it)
            }.onFailure {
                actions.postValue(ScoreboardAction.DisplayError(R.string.error_loading_scoreboard, this::loadScores))
            }
        }
    }

    sealed class ScoreboardAction {
        data class DisplayError(@StringRes val error: Int, val retry: () -> Unit): ScoreboardAction()
    }

}