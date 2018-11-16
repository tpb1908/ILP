package com.tpb.coinz.data.backend

import android.util.Log
import com.google.firebase.firestore.*
import com.tpb.coinz.data.coins.Coin
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestoreException



class FireStoreCoinCollection(private val store: FirebaseFirestore) : CoinCollection {

    private val collected = "collected"
    private val scoreboard = "scoreboard"
    private val scoreboardAll = "all"
    private val total = "total"

    override fun collectCoin(id: String, coin: Coin) {
        Log.i("FireStore", "Collecting ${coin.toMap()} for $id")
        store.collection(collected).document(id).update(coin.toMap())
    }

    private fun updateScoreboard(id: String, coin: Coin) {
        store.collection(scoreboardAll).document(id)
        val userScore = store.collection(scoreboardAll).document(id)
        store.runTransaction {
            val snapshot = it.get(userScore)
            var score = snapshot.getDouble(total) ?: 0.0
            score += coin.value
            it.set(userScore, mapOf(total to score), SetOptions.merge())
            // Set with merge will create document if it doesn't exist
            //it.update(userScore, total, score)
        }.addOnSuccessListener {
            Log.i("FireStore", "Successfully updated scoreboard for $id")
        }.addOnFailureListener {
            Log.e("FireStore", "Failed to update scoreboard for $id", it)
        }

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