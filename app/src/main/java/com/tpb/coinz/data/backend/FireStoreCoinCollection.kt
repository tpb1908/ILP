package com.tpb.coinz.data.backend

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.tpb.coinz.data.coins.Coin

class FireStoreCoinCollection(val store: FirebaseFirestore) : CoinCollection {

    private val collected = "collected"
    private val scoreboard = "scoreboard"
    private val scoreboardAll = "all"
    override fun collectCoin(id: String, coin: Coin) {
        //TODO: Result
        store.collection(collected).document(id).set(coin, SetOptions.merge())
        //store.collection(scoreboard).document(scoreboardAll).set(Any(), SetOptions.merge())
        //TODO: Write to scoreboard
    }

    override fun getCollectedCoins(id: String) {
        store.collection(collected).document(id)
    }
}