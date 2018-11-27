package com.tpb.coinz.data.backend

import android.util.Log
import com.google.firebase.firestore.*
import com.tpb.coinz.data.coins.Coin
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestoreException
import timber.log.Timber


class FireStoreCoinCollection(private val store: FirebaseFirestore) : CoinCollection {

    private val collected = "collected"
    private val scoreboard = "scoreboard"
    private val scoreboardAll = "all"
    private val total = "total"

    override fun collectCoin(id: String, coin: Coin) {
        Timber.i("Collecting ${coin.toMap()} for $id")
        store.collection(collected).document(id).update(coin.toMap())
    }

    private fun updateScoreboard(id: String, coin: Coin) {
        val userScore = store.collection(scoreboardAll).document(id)
        store.runTransaction {
            val snapshot = it.get(userScore)
            var score = snapshot.getDouble(total) ?: 0.0
            score += coin.value
            it.set(userScore, mapOf(total to score), SetOptions.merge())
            // Set with merge will create document if it doesn't exist
            //it.update(userScore, total, score)
        }.addOnSuccessListener {
            Timber.i("Successfully updated scoreboard for $id")
        }.addOnFailureListener {
            Timber.e(it, "Failed to update scoreboard for $id")
        }
    }

    override fun getCollectedCoins(id: String, listener: (List<Coin>) -> Unit) {
        Timber.i("Loading collected coins for $id")
        store.collection(collected).document(id).addSnapshotListener { d, e ->
            d?.data?.let { data ->
                data.keys.forEach { key ->
                    Timber.i("Coin $key map ${data[key]}")
                    //coins.add(Coin.fromMap(key, it[key] as MutableMap<String, Any>))
                }
                //TODO: Error handling
                listener(data.keys.map { Coin.fromMap(it, data[it] as MutableMap<String, Any>) })
            }
        }
    }

}