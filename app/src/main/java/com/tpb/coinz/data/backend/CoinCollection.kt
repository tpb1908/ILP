package com.tpb.coinz.data.backend

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.tpb.coinz.data.coins.Coin

interface CoinCollection {

    fun collectCoin(id: String, coin: Coin)

    fun getCollectedCoins(id: String, listener: (List<Coin>) -> Unit)



}