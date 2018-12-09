package com.tpb.coinz.data.coin.scoreboard

import com.google.firebase.firestore.FirebaseFirestore
import com.tpb.coinz.data.users.User
import com.tpb.coinz.data.util.Conversion
import com.tpb.coinz.data.util.FireStoreRegistration
import com.tpb.coinz.data.util.Registration

class FireStoreScoreboard(val store: FirebaseFirestore): Scoreboard {

    private val scores = "scores"
    private val all = "all"

    override fun increaseScore(user: User, increase: Double, callback: (Result<Double>) -> Unit) {
        val doc = store.collection(scores).document(user.uid)
        store.runTransaction { transaction ->
            val snap = transaction.get(doc)
            val currentScore = snap.getDouble(all) ?: 0.0
            transaction.update(doc, all, currentScore + increase)
        }
    }

    override fun getScore(user: User, listener: (Result<Double>) -> Unit): Registration =
            FireStoreRegistration(store.collection(scores).document(user.uid).addSnapshotListener { ds, exception ->
                if (ds != null) {
                    listener(Result.success(ds.getDouble(all) ?: 0.0))
                } else {
                    listener(Result.failure(Conversion.convertFireStoreException(exception)))
                }
            })
}