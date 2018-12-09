package com.tpb.coinz.data.coin.scoreboard

import com.tpb.coinz.data.users.User
import com.tpb.coinz.data.util.Registration

interface Scoreboard {

    fun increaseScore(user: User, increase: Double, callback: (Result<Double>) -> Unit)

    fun getScore(user: User, listener: (Result<Double>) -> Unit): Registration

}