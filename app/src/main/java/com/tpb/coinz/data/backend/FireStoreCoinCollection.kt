package com.tpb.coinz.data.backend

import android.util.Log
import com.google.firebase.firestore.*
import com.tpb.coinz.data.coins.Coin

class FireStoreCoinCollection(private val store: FirebaseFirestore) : CoinCollection {

    private val collected = "collected"
    private val scoreboard = "scoreboard"
    private val scoreboardAll = "all"

    override fun collectCoin(id: String, coin: Coin) {
        //TODO: Result
        Log.i("FireStore", "Collecting ${coin.toMap()} for $id")
        store.collection(collected).document(id).update(coin.toMap())
        //TODO: Write to scoreboard
    }

    override fun getCollectedCoins(id: String, listener: EventListener<DocumentSnapshot>) {
        store.collection(collected).document(id).addSnapshotListener(object: EventListener<DocumentSnapshot> {
            override fun onEvent(d: DocumentSnapshot?, e: FirebaseFirestoreException?) {
                d?.data?.forEach { s, any ->
                    Log.i("FireStore", "Key $s, Value $any")
                }
            }
        })
    }

}