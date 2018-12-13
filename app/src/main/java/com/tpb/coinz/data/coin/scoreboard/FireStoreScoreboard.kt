package com.tpb.coinz.data.coin.scoreboard

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.tpb.coinz.data.users.User
import com.tpb.coinz.data.util.Conversion
import com.tpb.coinz.data.util.FireStoreRegistration
import com.tpb.coinz.data.util.Registration
import timber.log.Timber

class FireStoreScoreboard(val store: FirebaseFirestore): Scoreboard {

    private val scoreboard = "scoreboard"
    private val all = "all"

    override fun increaseScore(user: User, increase: Double, callback: (Result<Double>) -> Unit) {
        val doc = store.collection(scoreboard).document(user.uid)
        store.runTransaction { transaction ->
            Timber.i("Updating score for ${user.email}")
            val snap = transaction.get(doc)
            val currentScore = snap.getDouble(all) ?: 0.0
            Timber.i("Current score $currentScore, Increase $increase")
            transaction.set(doc,
                    mapOf(all to currentScore + increase, "email" to user.email),
                    SetOptions.merge()
            )
        }.addOnCompleteListener {
            if (it.isSuccessful) {

            }
            Timber.i("Transaction complete ${it.isSuccessful}")
        }
    }

    override fun getScore(user: User, listener: (Result<Double>) -> Unit): Registration =
            FireStoreRegistration(store.collection(scoreboard).document(user.uid).addSnapshotListener { ds, exception ->
                if (ds != null) {
                    listener(Result.success(ds.getDouble(all) ?: 0.0))
                } else {
                    listener(Result.failure(Conversion.convertFireStoreException(exception)))
                }
            })

    override fun getScores(listener: (Result<List<Score>>) -> Unit): Registration =
            FireStoreRegistration(store.collection(scoreboard).addSnapshotListener { qs, exception ->
                if (qs != null) {
                    val scores = mutableListOf<Score>()
                    qs.documents.forEach { ds ->
                        scores.add(Score(User(ds.id, ds.getString("email")!!), ds.getDouble(all) ?: 0.0))
                    }
                    scores.sortByDescending { it.score }
                    listener(Result.success(scores))
                } else {
                    listener(Result.failure(Conversion.convertFireStoreException(exception)))
                }
            })
}